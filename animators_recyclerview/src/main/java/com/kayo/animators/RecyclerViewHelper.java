package com.kayo.animators;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

import com.kayo.animators.adapters.AnimationAdapter;
import com.kayo.animators.animators.BaseItemAnimator;

/**
 * Created by shilei on 17/1/20.
 * <pre>
 *      RecyclerView 动画帮助类
 * </pre>
 */

public class RecyclerViewHelper {

    @SuppressLint("StaticFieldLeak")
    private static RecyclerViewHelper instance;

    private RecyclerViewHelper() {
    }

    public static RecyclerViewHelper getHelper() {
        if (instance == null) {
            synchronized (RecyclerViewHelper.class) {
                if (instance == null) {
                    instance = new RecyclerViewHelper();
                }
            }
        }
        return instance;
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private BaseItemAnimator itemAnimator;
    private Interpolator itemInterpolator;
    private int itemDuration;
    private AnimationAdapter animationAdapter;
    private Interpolator adapterInterpolator;
    private int adapterDuration;

    public void bindRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void bindDataAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public void bindItemAnimatior(BaseItemAnimator itemAnimator) {
        this.itemAnimator = itemAnimator;
        notifyItemChanged();
    }

    public void bindItemInterpolator(Interpolator itemInterpolator) {
        this.itemInterpolator = itemInterpolator;
        notifyItemChanged();
    }

    public void setItemDuration(int itemDuration) {
        this.itemDuration = itemDuration;
        notifyItemChanged();
    }

    public void bindAnimationAdapter(AnimationAdapter adapter) {
        this.animationAdapter = adapter;
        notifyAdapterChanged();
    }

    public void bindAdapterInterpolator(Interpolator adapterInterpolator) {
        this.adapterInterpolator = adapterInterpolator;
        notifyAdapterChanged();
    }

    public void setAdapterDuration(int adapterDuration) {
        this.adapterDuration = adapterDuration;
        notifyAdapterChanged();
    }

    private void notifyItemChanged() {
        if (recyclerView == null) {
            return;
        }
        if (itemAnimator == null) {
            return;
        }
        this.recyclerView.setItemAnimator(this.itemAnimator);
        if (itemDuration >= 0) {
            this.itemAnimator.setAddDuration(this.itemDuration);
            this.itemAnimator.setRemoveDuration(this.itemDuration);
            this.itemAnimator.setMoveDuration(this.itemDuration);
            this.itemAnimator.setChangeDuration(this.itemDuration);
        }
        if (itemInterpolator != null) {
            this.itemAnimator.setInterpolator(this.itemInterpolator);
        }
    }

    private void notifyAdapterChanged() {
        if (recyclerView == null) {
            return;
        }
        if (animationAdapter == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        animationAdapter.bindAdapter(adapter);
        recyclerView.setAdapter(animationAdapter);
        if (adapterInterpolator != null) {
            animationAdapter.setInterpolator(adapterInterpolator);
        }
        if (adapterDuration >= 0) {
            animationAdapter.setDuration(adapterDuration);
        }
    }

    public void showNormalData() {
        if (recyclerView == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        recyclerView.setAdapter(adapter);
    }

    public void showAnimData() {
        if (recyclerView == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        if (animationAdapter == null) {
            return;
        }
        recyclerView.setAdapter(animationAdapter);
    }

    public void showData() {
        if (animationAdapter == null) {
            showNormalData();
        } else {
            showAnimData();
        }
    }

}