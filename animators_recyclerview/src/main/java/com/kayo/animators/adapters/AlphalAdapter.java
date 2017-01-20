package com.kayo.animators.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      透明过渡动画适配器
 * </pre>
 */

public class AlphalAdapter extends AnimationAdapter {

    private static final float DEFAULT_ALPHA_FROM = 0f;
    private final float mFrom;
    private String action = "alpha";

    public AlphalAdapter(float mFrom) {
        this.mFrom = mFrom;
    }

    public AlphalAdapter(RecyclerView.Adapter adapter) {
        this(adapter, DEFAULT_ALPHA_FROM);
    }

    public AlphalAdapter(RecyclerView.Adapter adapter, float from) {
        super(adapter);
        mFrom = from;
    }

    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, action, mFrom, 1f)};
    }
}
