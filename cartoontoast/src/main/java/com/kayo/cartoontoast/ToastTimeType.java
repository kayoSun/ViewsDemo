package com.kayo.cartoontoast;

import android.support.annotation.IntDef;
import android.support.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.support.annotation.RestrictTo.Scope.GROUP_ID;
import static com.kayo.cartoontoast.KartoonToast.LENGTH_SHORT;
import static com.kayo.cartoontoast.KartoonToast.LENGTH_LONG;

/**
 * Created by shilei on 17/2/13.
 * <pre>
 *      限定 KartoonToast 时长类型
 * </pre>
 */
@RestrictTo(GROUP_ID)
@IntDef({LENGTH_SHORT,LENGTH_LONG})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastTimeType {}
