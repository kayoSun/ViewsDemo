package com.kayo.animators.interfaces;

import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      列表动画设置接口
 *      实现列表动画的相关设置，可自行重写
 * </pre>
 */

public interface IAnimeSetting {
    /**
     * 设置动画执行时间
     */
    int getDuration();

    /**
     * 设置动画差值器
     */
    Interpolator getInterpolator();

    /**
     * 设置开始同化的首条目
     */
    int getStartPosition();

    /**
     * 设置是否只有首次展示的时候开启动画
     */
    boolean showFirstOnly();
}
