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

import java.util.Random;

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
    private int distanceY;
    private int tempDistanceY;
    private long ANIMATE_TO_START_DURATION = 200;
    private DecelerateInterpolator decelerateInterpolator;//在动画开始的地方速率改变比较慢，然后开始减速  差值器

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
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
                touched = handlMoveForPull(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handlActionUp();
                break;
        }
        return touched;
    }
    private boolean handlActionUp(){
        tempDistanceY = distanceY;
        toStartPosition.reset();
        toStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        toStartPosition.setInterpolator(decelerateInterpolator);
        headerContainer.clearAnimation();
        headerContainer.startAnimation(toStartPosition);

        return false;
    }

    private boolean handlMoveForPull(MotionEvent event) {
        float rawY = event.getRawY();
        float v = rawY - startY;
        distanceY += v;
        if (v>0){
            if (distanceY > headerHeight){
                distanceY = headerHeight;
            }
        }else {
            if (distanceY < -footerHeight){
                distanceY = -footerHeight;
            }
        }
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
        System.out.println("FreshLayout --> " + "anim = "+percent);
        distanceY = tempDistanceY -(int) (tempDistanceY*percent);
        requestLayout();
    }
    private final Animation toStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };
}
