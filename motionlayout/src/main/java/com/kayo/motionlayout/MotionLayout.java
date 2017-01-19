package com.kayo.motionlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilei on 16/12/22.
 * <pre>
 *
 * </pre>
 */
public class MotionLayout extends ViewGroup {
    private int HEADER_VIEW_HEIGHT = 200;// HeaderView height (dp) 头布局高度
    private int DEFAULT_CIRCLE_TARGET = 100;//下拉刷新 回跳的刷新距离

    private Handler handler;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final float DRAG_RATE = 0.6f;//手指滑动过程中的滑动距离丢失倍数
    private static final int INVALID_POINTER = -1;
    private static final int SCALE_DOWN_DURATION = 500;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 500;
    private static final int ANIMATE_TO_START_DURATION = 200;

    private int mActivePointerId = INVALID_POINTER;
    private int touchSlop;
    private int mMediumAnimationDuration;
    private int motionViewOffsetTop;//
    private float mTotalDragDistance = -1;//总共拖动的距离
    private float mInitialMotionY;//按下点的Y坐标
    private boolean refreshing = false;

    private boolean mOriginalOffsetCalculated = false;//原始偏移量计算
    private boolean mIsBeingDragged;//是否正在 被 拖动

    private final DecelerateInterpolator mDecelerateInterpolator;//在动画开始的地方速率改变比较慢，然后开始减速  差值器
    private static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};

    // RefreshLayout内的目标View，比如RecyclerView,ListView
    private View motionView;
    private HeaderViewContainer headContainer;
    private FooterViewContainer footContainer;

    private int mFrom;
    private int mOriginalOffsetTop;
    private int headerHeight;
    private int headerWidth;// headerView的宽度
    private int footerHeight;
    private int footerWidth;
    private int pushDistance = 0;//下拉距离
    // 最后停顿时的偏移量px，与DEFAULT_CIRCLE_TARGET正比
    private float mSpinnerFinalOffset;
    private boolean mReturningToStart;
    private boolean mNotify;
    private boolean autoCheck = true;//自动判断 是否滚动到顶状态
    private boolean isTop;//是否到顶

    public MotionLayout(Context context) {
        this(context, null);
    }

    public MotionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
        /**
         * getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
         */
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context
                .obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        headerWidth = display.getWidth();
        footerWidth = display.getWidth();
        //headerHeight = (int) (HEADER_VIEW_HEIGHT * metrics.density);
        headerHeight = UIUtils.dip2px(getContext(), HEADER_VIEW_HEIGHT * metrics.density);
        footerHeight = UIUtils.dip2px(getContext(), HEADER_VIEW_HEIGHT * metrics.density);
        createHeaderViewContainer();
        createFooterViewContainer();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
    }

    /**
     * 设置回跳刷新高度
     *
     * @param refreshHeight 刷新时的展示高度
     */
    public void setRefreshHeight(int refreshHeight) {
        this.DEFAULT_CIRCLE_TARGET = refreshHeight;
    }
    //==================头布局 相关======================

    /**
     * 添加头布局
     *
     * @param child 子控件
     */
    public void setHeaderView(View child) {
        if (child == null) {
            return;
        }
        if (headContainer == null) {
            return;
        }
        headContainer.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        headContainer.addView(child, layoutParams);
        headContainer.setVisibility(VISIBLE);
    }

    /*设置头布局高度*/
    public void setHeaderViewHeight(int headerViewHeight) {
        HEADER_VIEW_HEIGHT = headerViewHeight;
        ViewGroup.LayoutParams headerParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, UIUtils.dip2px(getContext(), headerViewHeight));
        headContainer.setLayoutParams(headerParams);
    }

    /**
     * 创建头布局的容器
     */
    private void createHeaderViewContainer() {
        headContainer = new HeaderViewContainer(getContext());
        headContainer.setVisibility(View.GONE);
        addView(headContainer);
    }
    //创建脚布局容器
    private void createFooterViewContainer(){
        footContainer = new FooterViewContainer(getContext());
        footContainer.setVisibility(GONE);
        addView(footContainer);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (this.refreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            this.refreshing = refreshing;
            if (this.refreshing) {
                animateOffsetToCorrectPosition(motionViewOffsetTop, mRefreshListener);
            } else {
                startScaleDownAnimation(motionViewOffsetTop, mRefreshListener);
            }
        }
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && this.refreshing != refreshing) {
            // scale and show
            this.refreshing = refreshing;
            int endTarget = 0;
            endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            setTargetOffsetTop(endTarget - motionViewOffsetTop,
                    true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    /**
     * 判断目标View是否滑动到顶部-还能否继续滑动
     */
    public boolean isChildScrollToTop() {
        if (autoCheck) {
            boolean isChildScrollToTop = false;
            if (motionView instanceof RecyclerView) {
                isChildScrollToTop = !(((RecyclerView) motionView).computeVerticalScrollOffset() > 0);
            } else {
                isChildScrollToTop = !ViewCompat.canScrollVertically(motionView, -1);
            }
            return isChildScrollToTop;
//            }
        } else {
            return isTop;
        }
    }

    /**
     * 判断目标View是否滑动到底部-还能否继续滑动
     */
    public boolean isChildScrollToBottom() {
        if (motionView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) motionView;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount();
            if (layoutManager instanceof LinearLayoutManager && count > 0) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    System.out.println("MotionLayout --> " + "到底");
                    return true;
                }
                System.out.println("MotionLayout --> " + "item = "+linearLayoutManager.findLastCompletelyVisibleItemPosition());
            }
            return false;
        }
        return false;
    }

    public void isTop(boolean isTop) {
        this.isTop = isTop;
    }

    public boolean isTop() {
        return isTop;
    }

    /**
     * 设置是否自动检测  到顶状态
     *
     * @param autoCheck 当false的时候  isTop参数开始请求作用
     */
    public void setAutoCheck(boolean autoCheck) {
        this.autoCheck = autoCheck;
    }

    /**
     * 下拉时，超过距离之后，弹回来的动画监听器
     */
    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (refreshing) {
                if (mNotify) {
                    notifyRefresh();
                }
            } else {
                headContainer.setVisibility(View.GONE);
                setTargetOffsetTop(mOriginalOffsetTop - motionViewOffsetTop, true);
            }
            motionViewOffsetTop = headContainer.getTop();
            updateDistance();
        }
    };

    /**
     * 更新回调
     */
    private void updateDistance() {
        int distance = motionViewOffsetTop + headContainer.getHeight();
        notifyPullDistance(distance);
    }

    /**
     * 孩子节点绘制的顺序
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        // 将新添加的View,放到最后绘制
//        if (mHeaderViewIndex < 0 && mFooterViewIndex < 0) {
//            return i;
//        }
//        if (i == childCount - 2) {
//            return mHeaderViewIndex;
//        }
//        if (i == childCount - 1) {
//            return mFooterViewIndex;
//        }
//        int bigIndex = mFooterViewIndex > mHeaderViewIndex ? mFooterViewIndex
//                : mHeaderViewIndex;
//        int smallIndex = mFooterViewIndex < mHeaderViewIndex ? mFooterViewIndex
//                : mHeaderViewIndex;
//        if (i >= smallIndex && i < bigIndex - 1) {
//            return i + 1;
//        }
//        if (i >= bigIndex || (i == bigIndex - 1)) {
//            return i + 2;
//        }
        return i;
    }

    //下拉刷新完成后 启动动画
    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        headContainer.setVisibility(View.VISIBLE);
        Animation mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime,
                                            Transformation t) {
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            headContainer.setAnimationListener(listener);
        }
        headContainer.clearAnimation();
        headContainer.startAnimation(mScaleAnimation);
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            headContainer.setAnimationListener(listener);
        }
        headContainer.clearAnimation();
        headContainer.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            headContainer.setAnimationListener(listener);
        }
        headContainer.clearAnimation();
        headContainer.startAnimation(mAnimateToStartPosition);
        resetTargetLayoutDelay(ANIMATE_TO_START_DURATION);
    }

    private void startScaleDownAnimation(int from, Animation.AnimationListener listener) {
        Animation mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                moveToStart(interpolatedTime);
            }
        };
        mFrom = from;
        mScaleDownAnimation.reset();
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        headContainer.setAnimationListener(listener);
        headContainer.clearAnimation();
        headContainer.startAnimation(mScaleDownAnimation);


    }

    /**
     * 确保mTarget不为空<br>
     * mTarget一般是可滑动的ScrollView,ListView,RecyclerView等
     */
    private void ensureTarget() {
        if (motionView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof HeaderViewContainer) && !(child instanceof FooterViewContainer)){
                    motionView = child;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        System.out.println("MotionLayout --> " + "left = "+left+"    right = "+right +"    width = "+width);
        if (getChildCount() == 0) {
            return;
        }
        if (motionView == null) {
            ensureTarget();
        }
        if (motionView == null) {
            return;
        }
        int distance = motionViewOffsetTop + headContainer.getHeight();
        final View child = motionView;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() + distance - pushDistance;// 根据偏移量distance更新
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop
                + childHeight);// 更新目标View的位置
        int headViewWidth = headContainer.getMeasuredWidth();
        int headViewHeight = headContainer.getMeasuredHeight();


        headContainer.layout((width / 2 - headViewWidth / 2),
                motionViewOffsetTop, (width / 2 + headViewWidth / 2),
                motionViewOffsetTop + headViewHeight);// 更新头布局的位置

        int footerL = 0;
        int footerT = height - pushDistance;
        int footerR = footContainer.getMeasuredWidth();
        int footerB = height+footContainer.getMeasuredHeight()-pushDistance;
        footContainer.layout(footerL,footerT,footerR,footerB);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (motionView == null) {
            ensureTarget();
        }
        if (motionView == null) {
            return;
        }
        motionView.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        headContainer.measure(
                MeasureSpec.makeMeasureSpec(headerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(headerHeight, MeasureSpec.EXACTLY));
        footContainer.measure(
                MeasureSpec.makeMeasureSpec(footerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(footerHeight, MeasureSpec.EXACTLY));


        if (!mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            motionViewOffsetTop = mOriginalOffsetTop = -headContainer.getMeasuredHeight();
            updateDistance();
        }
    }

    /**
     * 主要判断是否应该拦截子View的事件<br>
     * 如果拦截，则交给自己的OnTouchEvent处理<br>
     * 否者，交给子View处理<br>
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action = MotionEventCompat.getActionMasked(ev);//处理多点触控
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || refreshing
                || (!isChildScrollToTop())) {
            // 如果子View可以滑动，不拦截事件，交给子View处理-下拉刷新
            // 或者子View没有滑动到底部不拦截事件-上拉加载更多
            return false;
        }

        // 下拉刷新判断
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                headContainer.clearAnimation();//当点击是 移除头布局的所有动画
                setTargetOffsetTop(mOriginalOffsetTop - headContainer.getTop(), true);// 恢复HeaderView的初始位置
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;// 记录按下的位置
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                //下拉 操作
                if (isChildScrollToTop()) {
                    float yDiff = y - mInitialMotionY;// 计算下拉距离
                    if (yDiff > touchSlop && !mIsBeingDragged) {// 判断是否下拉的距离足够
                        mIsBeingDragged = true;// 正在下拉
                    }
                }
                //上拉 操作
                if (isChildScrollToBottom()){
                    float yDiff = mInitialMotionY - y;// 计算上拉距离
                    if (yDiff > touchSlop && !mIsBeingDragged) {// 判断是否下拉的距离足够
                        mIsBeingDragged = true;// 正在上拉
                    }
                }

                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            default:
                break;
        }

        return mIsBeingDragged;// 如果正在拖动，则拦截子View的事件
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev,
                activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart
                || (!isChildScrollToTop())) {
            // 如果子View可以滑动，不拦截事件，交给子View处理
            return false;
        }
        if (isChildScrollToTop()) {//如果是上 顶部
            return handlerPullTouchEvent(ev, action);
        } else if (isChildScrollToBottom()) {//如果是下 底部
            System.out.println("MotionLayout --> " + "上拉");
            return handlerPushTouchEvent(ev, action);
        }
        return false;
    }

    //解析下拉动作
    private boolean handlerPullTouchEvent(MotionEvent ev, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
                        mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                //限定滑动距离 最大为头布局高度 add shilei
                if (overscrollTop >= UIUtils.dip2px(getContext(), HEADER_VIEW_HEIGHT)) {
                    return false;
                }
                if (mIsBeingDragged) {
                    float originalDragPercent = overscrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
                    float slingshotDist = mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math
                            .pow((tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;

                    int targetY = mOriginalOffsetTop
                            + (int) ((slingshotDist * dragPercent) + extraMove);
                    if (headContainer.getVisibility() != View.VISIBLE) {
                        headContainer.setVisibility(View.VISIBLE);
                    }
                    if (overscrollTop < mTotalDragDistance) {
                        notifyPullEnable(false);
                    } else {
                        notifyPullEnable(true);
                    }
                    setTargetOffsetTop(targetY - motionViewOffsetTop, true);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
                        mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true /* notify */);
                } else {
                    refreshing = false;
                    Animation.AnimationListener listener = new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            startScaleDownAnimation(motionViewOffsetTop, null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                    };
                    animateOffsetToStartPosition(motionViewOffsetTop, listener);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    //解析上拉动作
    private boolean handlerPushTouchEvent(MotionEvent ev, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
                        mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollBottom = (mInitialMotionY - y) * DRAG_RATE;
                if (mIsBeingDragged) {
                    pushDistance = (int) overscrollBottom;
                    updateFooterViewPosition();
                }

                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                return false;
            }
        }
        return true;
    }

    private void updateFooterViewPosition() {
        footContainer.setVisibility(View.VISIBLE);
        footContainer.bringToFront();
        //针对4.4及之前版本的兼容
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            footContainer.getParent().requestLayout();
        }
        footContainer.offsetTopAndBottom(-pushDistance);

    }

    /**
     * 重置motionView位置
     *
     * @param delay 延迟 时间
     */
    public void resetTargetLayoutDelay(int delay) {
        if (null == handler) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetTargetLayout();
            }
        }, delay);
    }

    /**
     * 重置motionView位置
     */
    public void resetTargetLayout() {
        final int width = getMeasuredWidth();
        final View child = motionView;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = child.getWidth() - getPaddingLeft()
                - getPaddingRight();
        final int childHeight = child.getHeight() - getPaddingTop()
                - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop
                + childHeight);

        int headViewWidth = headContainer.getMeasuredWidth();
        int headViewHeight = headContainer.getMeasuredHeight();
        headContainer.layout((width / 2 - headViewWidth / 2),
                -headViewHeight, (width / 2 + headViewWidth / 2), 0);// 更新头布局的位置
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            endTarget = (int) (mSpinnerFinalOffset - Math
                    .abs(mOriginalOffsetTop));
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - headContainer.getTop();
            setTargetOffsetTop(offset, false);
        }
    };

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - headContainer.getTop();
        setTargetOffsetTop(offset, false);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        headContainer.bringToFront();
        headContainer.offsetTopAndBottom(offset);//设置偏移量
        motionViewOffsetTop = headContainer.getTop();
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
        updateDistance();
    }

    private void setTargetOffsetBottom(int offset,boolean requiresUpdate){

    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev,
                    newPointerIndex);
        }
    }


    //==============================监听相关================================
    private List<IRefreshListener> refreshListeners;

    //通知 监听器 刷新开始
    private void notifyRefresh() {
        if (null == refreshListeners || refreshListeners.size() < 1) {
            return;
        }
        for (IRefreshListener refreshListener : refreshListeners) {
            refreshListener.onRefresh();
        }
    }

    //通知 监听器 下拉距离
    private void notifyPullDistance(int distance) {
        if (null == refreshListeners || refreshListeners.size() < 1) {
            return;
        }
        for (IRefreshListener refreshListener : refreshListeners) {
            refreshListener.onPullDistance(distance);
        }
    }

    //通知 监听器 下拉到达刷新距离
    private void notifyPullEnable(boolean enable) {
        if (null == refreshListeners || refreshListeners.size() < 1) {
            return;
        }
        for (IRefreshListener refreshListener : refreshListeners) {
            refreshListener.onPullEnable(enable);
        }
    }

    public void addRefreshListener(IRefreshListener refreshListener) {
        if (null == refreshListeners) {
            refreshListeners = new ArrayList<>();
        }
        refreshListeners.add(refreshListener);
    }

    public void removeRefreshListener(IRefreshListener refreshListener) {
        if (null != refreshListeners) {
            refreshListeners.remove(refreshListener);
        }
    }

    public void clearRefreshListeners() {
        if (null != refreshListeners) {
            refreshListeners.clear();
        }
    }

}
