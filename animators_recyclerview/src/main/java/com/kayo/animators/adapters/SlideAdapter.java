package com.kayo.animators.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      滑入过渡动画适配器
 * </pre>
 */

public class SlideAdapter extends AnimationAdapter {

    public final static int LEFT = 1;
    public final static int RIGHT = 2;
    public final static int BOTTOM = 3;

    private String action = "translationX";
    private int orientation = LEFT;//滑入方向

    public SlideAdapter() {
    }

    public SlideAdapter(int orientation) {
        this.orientation = orientation;
    }

    public SlideAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        super(adapter);
    }

    /**
     * @param adapter     数据适配器
     * @param orientation <ul>
     *                    <li>{@link SlideAdapter#LEFT}</li>
     *                    <li>{@link SlideAdapter#RIGHT}</li>
     *                    <li>{@link SlideAdapter#BOTTOM}</li>
     *                    </ul>
     */
    public SlideAdapter(RecyclerView.Adapter adapter, int orientation) {
        super(adapter);
        this.orientation = orientation;
    }

    @Override
    public Animator[] getAnimators(View view) {
        Animator[] animators;
        switch (orientation) {
            case LEFT:
                animators = new Animator[]{ObjectAnimator.ofFloat(view, action, -view.getRootView().getWidth(), 0)};
                break;
            case RIGHT:
                animators = new Animator[]{ObjectAnimator.ofFloat(view, action, view.getRootView().getWidth(), 0)};
                break;
            case BOTTOM:
                animators = new Animator[]{ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)};
                break;
            default:
                animators = new Animator[]{ObjectAnimator.ofFloat(view, action, -view.getRootView().getWidth(), 0)};
                break;
        }
        return animators;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
