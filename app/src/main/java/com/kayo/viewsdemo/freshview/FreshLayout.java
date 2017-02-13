package com.kayo.viewsdemo.freshview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.kayo.motionlayout.FooterViewContainer;
import com.kayo.motionlayout.HeaderViewContainer;

/**
 * Created by shilei on 17/2/10.
 * <pre>
 *
 * </pre>
 */

public class FreshLayout extends ViewGroup {

    private int headerWidth;
    private int headerHeight;
    private int footerWidth;
    private int footerHeight;
    private float startY;
    private int distanceY;//当前view的位置
    private int tempDistanceY;//当手指抬起时的位置
    private long ANIMATE_TO_START_DURATION = 200;//返回开始位置时 的动画执行时间
    private DecelerateInterpolator decelerateInterpolator;//在动画开始的地方速率改变比较慢，然后开始减速  差值器
    private boolean isRefresh;
    private boolean isLoadmore;

    public FreshLayout(Context context) {
        super(context);
        initView();
    }

    public FreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private HeaderViewContainer headerContainer;//头
    private FooterViewContainer footerContainer;//脚
    private View dataView;//身

    public void initView() {
        WindowManager systemService = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = systemService.getDefaultDisplay().getWidth();
        headerWidth = width;
        footerWidth = width;
        headerHeight = headerWidth / 2;
        footerHeight = footerWidth / 2;
        headerContainer = new HeaderViewContainer(getContext());
        headerContainer.setBackgroundColor(Color.BLUE);
        addView(headerContainer);
        footerContainer = new FooterViewContainer(getContext());
        footerContainer.setBackgroundColor(Color.BLACK);
        addView(footerContainer);
        headerContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPosition();
            }
        });
        footerContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPosition();
            }
        });
        decelerateInterpolator = new DecelerateInterpolator();
    }

    /**
     * 确认 如果包含 目标view
     */
    public void ifHaveDataView() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (!(childAt instanceof HeaderViewContainer) && !(childAt instanceof FooterViewContainer)) {
                if (dataView == null) {
                    dataView = childAt;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ifHaveDataView();
        if (dataView == null) {
            return;
        }
        dataView.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        headerContainer.measure(
                MeasureSpec.makeMeasureSpec(headerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(headerHeight, MeasureSpec.EXACTLY));
        footerContainer.measure(
                MeasureSpec.makeMeasureSpec(footerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(footerHeight, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ifHaveDataView();
        if (dataView == null) {
            return;
        }
        int rootWidth = getMeasuredWidth();
        int rootHeight = getMeasuredHeight();
        int dataViewL = getPaddingLeft();
        int dataViewR = dataViewL + (rootWidth - getPaddingLeft() - getPaddingRight());
        int dataViewT = getPaddingTop() + distanceY;
        int dataViewB = dataViewT + (rootHeight - getPaddingTop() - getPaddingBottom());
        dataView.layout(dataViewL, dataViewT, dataViewR, dataViewB);
        int headViewL = getPaddingLeft();
        int headViewR = headViewL + (rootWidth - getPaddingLeft() - getPaddingRight());
        int headViewT = -headerContainer.getMeasuredHeight() + distanceY;
        int headViewB = distanceY;
        headerContainer.layout(headViewL, headViewT, headViewR, headViewB);
        int footViewL = getPaddingLeft();
        int footViewR = footViewL + (rootWidth - getPaddingLeft() - getPaddingRight());
        int footViewT = dataViewB;
        int footViewB = footViewT + footerContainer.getMeasuredHeight();
        footerContainer.layout(footViewL, footViewT, footViewR, footViewB);

    }


    /**
     * 判断是否是顶部或底部
     * 如果是 返回true
     * @return 是顶部或底部 返回true
     */
    private boolean isTopOrBottom(){
        return true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("FreshLayout --> " + "dispatchTouchEvent");
        int action = ev.getAction();
//        if (action == MotionEvent.ACTION_MOVE){
//            return !isTopOrBottom();
//        }else {
//            return false;
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touched = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果子View可以滑动，不拦截事件，交给子View处理
                if (touched = isTopOrBottom()){
                    handlMoveAction(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handlActionUp();
                startY = getY();
                touched = super.onTouchEvent(event);
                break;
        }
        return touched;
    }

    //解析手指 抬起动作
    private boolean handlActionUp(){
        if (isRefresh){
            notifyRefresh();
            return false;
        }
        if (isLoadmore){
            notifyLoadMore();
            return false;
        }
        tempDistanceY = distanceY;
        toStartPosition.reset();
        toStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        toStartPosition.setInterpolator(decelerateInterpolator);
        headerContainer.clearAnimation();
        headerContainer.startAnimation(toStartPosition);
        return false;
    }

    //解析 拖动动作
    private boolean handlMoveAction(MotionEvent event) {
        float rawY = event.getRawY();
        float v = rawY - startY;
        distanceY += v;
        if (v>0){
            isLoadmore = false;
            if (distanceY > headerHeight){
                distanceY = headerHeight;
                v = 0;
            }
            if (distanceY > headerHeight*.7){
                isRefresh = true;
            }else {
                isRefresh = false;
            }
        }else {
            isRefresh = false;
            if (distanceY < -footerHeight){
                distanceY = -footerHeight;
                v = 0;
            }
            if (Math.abs(distanceY) > footerHeight*.7){
                isLoadmore = true;
            }else {
                isLoadmore = false;
            }
        }
        updateDistance();
        startY = event.getRawY();
        dataView.offsetTopAndBottom((int) v);
        headerContainer.offsetTopAndBottom((int) v);
        footerContainer.offsetTopAndBottom((int) v);
        return true;
    }

    private void resetPosition(){
        distanceY = 0;
        requestLayout();
    }

    private void moveToStart(float percent){
        distanceY = tempDistanceY -(int) (tempDistanceY*percent);
        if (percent == 1){
            resetPosition();
        }else {
            requestLayout();
        }
    }
    private final Animation toStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void notifyRefresh(){
        System.out.println("FreshLayout --> " + "刷新");

    }
    private void notifyLoadMore(){
        System.out.println("FreshLayout --> " + "加载更多");
    }
    private void updateDistance(){

    }
}
