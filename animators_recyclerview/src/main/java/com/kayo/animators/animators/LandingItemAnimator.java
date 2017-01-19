package com.kayo.animators.animators;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *
 * </pre>
 */

public class LandingItemAnimator extends BaseItemAnimator {

    private Interpolator mInterpolator;

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();
    }

    @Override
    public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        super.preAnimateAddImpl(holder);
        ViewCompat.setAlpha(holder.itemView, 0);
        ViewCompat.setScaleX(holder.itemView, 1.5f);
        ViewCompat.setScaleY(holder.itemView, 1.5f);
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .alpha(0)
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }
}
