package com.kayo.cartoontoast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by shilei on 17/2/13.
 * <pre>
 *     KartoonToast 建造者
 *     使用 方法 ：
 *     KartoonBuilder.builder()
 *                      .context(this)
 *                      .message("测试文案")
 *                      .type(KartoonToast.WARNING)
 *                      .duration(KartoonToast.LENGTH_SHORT)
 *                      .show();
 *    其中context为必传参数
 * </pre>
 */

public class KartoonBuilder {
    private Context context;
    private String msg;
    @ToastUIType
    private int type = KartoonToast.DEFAULT;
    @ToastTimeType
    private int duration = KartoonToast.LENGTH_SHORT;

    @SuppressLint("StaticFieldLeak")
    private static KartoonBuilder instance;

    private KartoonBuilder() {
    }

    public static KartoonBuilder builder() {
        if (instance == null) {
            synchronized (KartoonBuilder.class) {
                if (instance == null) {
                    instance = new KartoonBuilder();
                }
            }
        }
        return instance;
    }

    public KartoonBuilder context(Context context) {
        this.context = context;
        return this;
    }

    public KartoonBuilder message(String msg) {
        this.msg = msg;
        return this;
    }

    public KartoonBuilder type(@ToastUIType int type) {
        this.type = type;
        return this;
    }

    public KartoonBuilder duration(@ToastTimeType int time) {
        this.duration = time;
        return this;
    }

    public void show() {
        if (context == null) {
            throw new IllegalArgumentException("KartoonBuilder context对象不能为空");
        }
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        KartoonToast.toast(context, msg, duration, type);
    }

    public void cancel() {
        KartoonToast.cancelToast();
    }

    public KartoonBuilder reset() {
        context = null;
        msg = null;
        type = KartoonToast.DEFAULT;
        duration = KartoonToast.LENGTH_SHORT;
        return this;
    }
}
