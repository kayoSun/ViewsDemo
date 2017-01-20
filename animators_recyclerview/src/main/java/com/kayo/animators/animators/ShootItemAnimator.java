package com.kayo.animators.animators;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      射击动画
 * </pre>
 */

public class ShootItemAnimator extends BaseItemAnimator {

    private float mTension = 2;
    private int orientation = Orientation.LEFT;

    public ShootItemAnimator(){}

    public ShootItemAnimator(float mTension) {
        this.mTension = mTension;
    }

    public ShootItemAnimator(int orientation) {
        this.orientation = orientation;
    }

    public ShootItemAnimator(float mTension, int orientation) {
        this.mTension = mTension;
        this.orientation = orientation;
    }

    public void setmTension(float mTension) {
        this.mTension = mTension;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder))
                .setInterpolator(new OvershootInterpolator(mTension))
                .setStartDelay(getAddDelay(holder))
                .start();
    }

    @Override
    public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateAddImpl(holder);
        switch (orientation){
            case Orientation.LEFT:
                ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
                break;
            case Orientation.RIGHT:
                ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
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
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder));
        switch (orientation){
            case Orientation.LEFT:
                animate.translationX(-holder.itemView.getRootView().getWidth());
                break;
            case Orientation.RIGHT:
                animate.translationX(holder.itemView.getRootView().getWidth());
                break;
            default:
                animate.translationX(-holder.itemView.getRootView().getWidth());
                break;
        }
        animate.start();
    }

    @Override
    public void setInterpolator(Interpolator mInterpolator) {

    }
}
