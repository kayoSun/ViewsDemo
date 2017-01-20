package com.kayo.animators.adapters;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.kayo.animators.ViewAnimationHelper;
import com.kayo.animators.interfaces.IAnimateAdapter;
import com.kayo.animators.interfaces.IAnimeSetting;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      动画数据适配器 基类
 * </pre>
 */

public abstract class AnimationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IAnimateAdapter {

    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private IAnimeSetting animeSetting;

    private boolean isFirstOnly = true;

    public AnimationAdapter(){
        this(null);
    }

    public AnimationAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
       this(adapter,null);
    }

    public AnimationAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter,IAnimeSetting animeSetting){
       bindAdapter(adapter);
       bindAnimeSetting(animeSetting);
    }

    @Override
    public void bindAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void bindAnimeSetting(IAnimeSetting animeSetting) {
        this.animeSetting = animeSetting;
        if (null != animeSetting){
            int duration = animeSetting.getDuration();
            if (duration>=0){
                mDuration = duration;
            }
            Interpolator interpolator = animeSetting.getInterpolator();
            if (null != interpolator){
                mInterpolator = interpolator;
            }
            isFirstOnly = animeSetting.showFirstOnly();
            mLastPosition = animeSetting.getStartPosition();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mAdapter.onBindViewHolder(holder, position);

        int adapterPosition = holder.getAdapterPosition();
        if (!isFirstOnly || adapterPosition > mLastPosition) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setInterpolator(mInterpolator);
                anim.setDuration(mDuration).start();
            }
            mLastPosition = adapterPosition;
        } else {
            ViewAnimationHelper.clearAnimStatus(holder.itemView);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mAdapter.onViewRecycled(holder);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public void setDuration(int duration) {
        mDuration = duration;
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override
    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    @Override
    public abstract Animator[] getAnimators(View view);

    @Override
    public void showFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.Adapter<RecyclerView.ViewHolder> getWrappedAdapter() {
        return mAdapter;
    }

}
