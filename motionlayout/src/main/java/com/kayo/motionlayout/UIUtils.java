package com.kayo.motionlayout;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by shilei on 16/12/22.
 * <pre>
 *  UI工具类
 * <pre>
 */

public class UIUtils {

    /**
     * 获取设备像素密度
     * @return
     */
    public static float getDensity(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * dp转px
     * @param dipValue 需要转换的值
     * @return 转换后的值
     */
    public static int dip2px(Context context,float dipValue) {
        float scale = getDensity(context);
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * dp转px 并按比例放大
     * @param dipValue 需要转换的值
     * @param toscale 转换倍数
     * @return 转换后的值
     */
    public static int dip2px(Context context,float dipValue, float toscale) {
        float scale = getDensity(context);
        return (int) ((dipValue * scale + 0.5f) * toscale);
    }
    /**
     * px转dp
     * @param pxValue 需要转换的值
     * @return 转换后的值
     */
    public static int px2dip(Context context,float pxValue) {
        float scale = getDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue 需要转换的值
     * @return 转换后的值
     */
    public static int px2sp(Context context,float pxValue) {
        float scale = getDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue 需要转换的值
     * @return 转换后的值
     */
    public static int sp2px(Context context,float spValue) {
        float scale = getDensity(context);
        return (int) (spValue * scale + 0.5f);
    }

    public static int getDisplayWidth(Context context){
        return getWindowManager(context).getDefaultDisplay().getWidth();
    }

    public static int getDisplayHeight(Context context){
        return getWindowManager(context).getDefaultDisplay().getHeight();
    }

    public static WindowManager getWindowManager(Context context){
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

}
