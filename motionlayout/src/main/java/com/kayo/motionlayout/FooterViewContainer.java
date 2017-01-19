package com.kayo.motionlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.FrameLayout;

/**
 * Created by shilei on 16/12/22.
 * <pre>
 *
 * </pre>
 */

public class FooterViewContainer extends FrameLayout {

    public FooterViewContainer(Context context) {
        super(context);
    }

    public FooterViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private Animation.AnimationListener mListener;

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }
}
