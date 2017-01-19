package com.kayo.animators.interfaces;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      动画数据适配器接口
 * </pre>
 */

public interface IAnimateAdapter {
    void setDuration(int duration);
    void setInterpolator(Interpolator interpolator);
    void setStartPosition(int start);
    void showFirstOnly(boolean firstOnly);
    Animator[] getAnimators(View view);
    RecyclerView.Adapter<RecyclerView.ViewHolder> getWrappedAdapter();
}
