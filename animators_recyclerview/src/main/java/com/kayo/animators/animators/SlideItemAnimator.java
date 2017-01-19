package com.kayo.animators.animators;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      滑入动画
 * </pre>
 */

public class SlideItemAnimator extends BaseItemAnimator {

    private int orientation = Orientation.DEFAULT;//滑入方向
    private Interpolator mInterpolator;

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(holder.itemView);
        animate.setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder));
        switch (orientation) {
            case Orientation.LEFT:
                animate.translationX(0);
                break;
            case Orientation.RIGHT:
                animate.translationX(0);
                break;
            case Orientation.UP:
                animate.translationY(0)
                        .alpha(1);
                break;
            case Orientation.DOWN:
                animate.translationY(0)
                        .alpha(1);
                break;
            default:
                animate.translationX(0);
                break;
        }
        animate.start();
    }

    @Override
    public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateAddImpl(holder);
        switch (orientation) {
            case Orientation.LEFT:
                ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
                break;
            case Orientation.RIGHT:
                ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
                break;
            case Orientation.UP:
                ViewCompat.setTranslationY(holder.itemView, holder.itemView.getHeight());
                ViewCompat.setAlpha(holder.itemView, 0);
                break;
            case Orientation.DOWN:
                ViewCompat.setTranslationY(holder.itemView, -holder.itemView.getHeight());
                ViewCompat.setAlpha(holder.itemView, 0);
                break;
            default:
                ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
                break;
        }
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(holder.itemView);
        animate.setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder));
        switch (orientation) {
            case Orientation.LEFT:
                animate.translationX(-holder.itemView.getRootView().getWidth());
                break;
            case Orientation.RIGHT:
                animate.translationX(holder.itemView.getRootView().getWidth());
                break;
            case Orientation.UP:
                animate.translationY(holder.itemView.getHeight())
                        .alpha(0);
                break;
            case Orientation.DOWN:
                animate.translationY(-holder.itemView.getHeight())
                        .alpha(0);
                break;
            default:
                animate.translationX(-holder.itemView.getRootView().getWidth());
                break;
        }
        animate.start();
    }
}
