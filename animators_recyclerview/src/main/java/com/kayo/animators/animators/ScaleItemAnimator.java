package com.kayo.animators.animators;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      缩放动画
 * </pre>
 */

public class ScaleItemAnimator extends BaseItemAnimator {

    private int orientation = Orientation.DEFAULT;//滑入方向
    private Interpolator mInterpolator;

    public ScaleItemAnimator(){}

    public ScaleItemAnimator(int orientation) {
        this.orientation = orientation;
    }

    public ScaleItemAnimator(Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }

    public ScaleItemAnimator(int orientation, Interpolator mInterpolator) {
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
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);
        switch (orientation){
            case Orientation.LEFT:
                ViewCompat.setPivotX(holder.itemView, 0);
                break;
            case Orientation.RIGHT:
                ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
                break;
            case Orientation.UP:
                holder.itemView.setPivotY(0);
                break;
            case Orientation.DOWN:
                holder.itemView.setPivotY(holder.itemView.getHeight());
                break;
            default:
                break;
        }
    }

    @Override
    public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateRemoveImpl(holder);
        switch (orientation){
            case Orientation.LEFT:
                ViewCompat.setPivotX(holder.itemView, 0);
                break;
            case Orientation.RIGHT:
                ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
                break;
            case Orientation.UP:
                holder.itemView.setPivotY(0);
                break;
            case Orientation.DOWN:
                holder.itemView.setPivotY(holder.itemView.getHeight());
                break;
            default:
                break;
        }
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .scaleX(1)
                .scaleY(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();

    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .scaleX(0)
                .scaleY(0)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }
}
