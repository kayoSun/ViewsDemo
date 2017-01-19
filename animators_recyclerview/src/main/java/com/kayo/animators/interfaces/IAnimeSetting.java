package com.kayo.animators.interfaces;

import android.view.animation.Interpolator;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *
 * </pre>
 */

public interface IAnimeSetting {
    int getDuration();
    Interpolator getInterpolator();
    int getStartPosition();
    boolean showFirstOnly();
}
