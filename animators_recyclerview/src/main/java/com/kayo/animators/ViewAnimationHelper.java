package com.kayo.animators;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *      View 动画 帮助类
 * </pre>
 */

public class ViewAnimationHelper {
    /**
     * 重置View的所有动画状态
     * @param view 被操作view
     */
    public static void clearAnimStatus(View view) {
        if (null == view){
            return;
        }
        ViewCompat.setAlpha(view, 1);
        ViewCompat.setScaleY(view, 1);
        ViewCompat.setScaleX(view, 1);
        ViewCompat.setTranslationY(view, 0);
        ViewCompat.setTranslationX(view, 0);
        ViewCompat.setRotation(view, 0);
        ViewCompat.setRotationY(view, 0);
        ViewCompat.setRotationX(view, 0);
        ViewCompat.setPivotY(view, view.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(view, view.getMeasuredWidth() / 2);
        ViewCompat.animate(view).setInterpolator(null).setStartDelay(0);
    }

    /**
     * 取消view的动画效果
     * @param view 被操作view
     */
    public static void cancelAnim(View view){
        if (view ==null){
            return;
        }
        ViewCompat.animate(view).cancel();
    }

}
