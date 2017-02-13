package com.kayo.cartoontoast;

import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.support.annotation.RestrictTo.Scope.GROUP_ID;
import static com.kayo.cartoontoast.KartoonToast.SUCCESS;
import static com.kayo.cartoontoast.KartoonToast.WARNING;
import static com.kayo.cartoontoast.KartoonToast.ERROR;
import static com.kayo.cartoontoast.KartoonToast.INFO;
import static com.kayo.cartoontoast.KartoonToast.DEFAULT;

/**
 * Created by shilei on 17/2/13.
 * <pre>
 *      限定 KartoonToast 显示类型
 * </pre>
 */
@RestrictTo(GROUP_ID)
@IntDef({SUCCESS,WARNING,ERROR,INFO,DEFAULT})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastUIType {}
