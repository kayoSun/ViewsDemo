package com.kayo.animators.animators;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      翻滚动画
 * </pre>
 */

public class FlipItemAnimator extends BaseItemAnimator {
    private int orientation = Orientation.DEFAULT;//滑入方向
    private Interpolator mInterpolator;

    public FlipItemAnimator(){}

    public FlipItemAnimator(int orientation) {
        this.orientation = orientation;
    }

    public FlipItemAnimator(Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }

    public FlipItemAnimator(int orientation, Interpolator mInterpolator) {
        this.orientation = orientation;
        this.mInterpolator = mInterpolator;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public void setInterpolator(Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }

    @Override
    public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateAddImpl(holder);
        switch (orientation){
            case Orientation.LEFT:
                ViewCompat.setRotationY(holder.itemView, 90);
                break;
            case Orientation.RIGHT:
                ViewCompat.setRotationY(holder.itemView, -90);
                break;
            case Orientation.UP:
                ViewCompat.setRotationX(holder.itemView, 90);
                break;
            case Orientation.DOWN:
                ViewCompat.setRotationX(holder.itemView, -90);
                break;
            default:
                ViewCompat.setRotationY(holder.itemView, 90);
                break;
        }
    }

    @Override
    public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateRemoveImpl(holder);
        switch (orientation){
            case Orientation.LEFT:
                break;
            case Orientation.RIGHT:
                break;
            case Orientation.UP:
                break;
            case Orientation.DOWN:
                break;
            default:
                break;
        }
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(holder.itemView);
        animate.setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder));
        switch (orientation){
            case Orientation.LEFT:
                animate.rotationY(0);
                break;
            case Orientation.RIGHT:
                animate.rotationY(0);
                break;
            case Orientation.UP:
                animate.rotationX(0);
                break;
            case Orientation.DOWN:
                animate.rotationX(0);
                break;
            default:
                animate.rotationY(0);
                break;
        }
       animate.start();
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewPropertyAnimatorCompat animate = ViewCompat.animate(holder.itemView);
        animate.setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder));
        switch (orientation){
            case Orientation.LEFT:
                animate.rotationY(90);
                break;
            case Orientation.RIGHT:
                animate.rotationY(-90);
                break;
            case Orientation.UP:
                animate.rotationX(90);
                break;
            case Orientation.DOWN:
                animate.rotationX(-90);
                break;
            default:
                animate.rotationY(90);
                break;
        }
        animate.start();
    }

}
