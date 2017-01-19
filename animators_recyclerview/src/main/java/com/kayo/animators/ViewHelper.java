package com.kayo.animators;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      View 帮助类
 * </pre>
 */

public class ViewHelper {
    /**
     * 清除View的所有动画状态
     * @param v 被操作view
     */
    public static void clearAnimStatus(View v) {
        if (null == v){
            return;
        }
        ViewCompat.setAlpha(v, 1);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 1);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null).setStartDelay(0);
    }

    public static void cancelAnim(View view){
        if (view ==null){
            return;
        }
        ViewCompat.animate(view).cancel();
    }
}
