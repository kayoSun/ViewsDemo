package com.kayo.cartoontoast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.kayo.cartoontoast.views.DefaultToastView;
import com.kayo.cartoontoast.views.ErrorToastView;
import com.kayo.cartoontoast.views.InfoToastView;
import com.kayo.cartoontoast.views.SuccessToastView;
import com.kayo.cartoontoast.views.WarningToastView;

import static java.security.AccessController.getContext;

/**
 * Created by Kayo on 2016/8/10.
 * 自定义 toast类  实现卡通动画效果
 * 使用直接调用相关方法即可
 */
public class KartoonToast {
    @SuppressWarnings("WeakerAccess")
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;

    @SuppressWarnings("WeakerAccess")
    public static final int SUCCESS = 1;
    @SuppressWarnings("WeakerAccess")
    public static final int WARNING = 2;
    @SuppressWarnings("WeakerAccess")
    public static final int ERROR = 3;
    @SuppressWarnings("WeakerAccess")
    public static final int INFO = 4;
    @SuppressWarnings("WeakerAccess")
    public static final int DEFAULT = 5;

    private static Toast toast;

    @SuppressWarnings("unused")
    public static void toast(Context context, String msg, @ToastUIType int type) {
        toast(context, msg, LENGTH_SHORT, type);
    }

    @SuppressWarnings("WeakerAccess")
    public static void toast(Context context, String msg, int length, @ToastUIType int type) {

        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        toast.setDuration(length);

        @SuppressLint("InflateParams")
        View layout = LayoutInflater.from(context).inflate(R.layout.cartoon_root_layout, null, false);
        TextView text = (TextView) layout.findViewById(R.id.toastMessage);
        text.setText(msg);
        LinearLayout cartoonViewContainer = (LinearLayout) layout.findViewById(R.id.cartton_view_container);
        int width = dip2px(context, 50);
        int margin = dip2px(context, 10);
        //noinspection SuspiciousNameCombination
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        params.bottomMargin = margin;
        params.topMargin = margin;
        params.leftMargin = margin;
        params.rightMargin = margin;

        switch (type) {
            case SUCCESS: {
                createSuccessView(context, text, cartoonViewContainer, params);
                break;
            }
            case WARNING: {
                createWarningView(context, text, cartoonViewContainer, params);
                break;
            }
            case ERROR: {
                createErrorView(context, text, cartoonViewContainer, params);
                break;
            }
            case INFO: {
                createInfoView(context, text, cartoonViewContainer, params);
                break;
            }
            case DEFAULT: {
                createDefaultView(context, text, cartoonViewContainer, params);
                break;
            }
            default:
                createDefaultView(context, text, cartoonViewContainer, params);
                break;
        }
        toast.setView(layout);
        toast.show();
    }

    @SuppressWarnings("unused")
    public static void toastSuccess(Context context, String msg) {
        toast(context, msg, LENGTH_SHORT, SUCCESS);
    }

    @SuppressWarnings("unused")
    public static void toastWarning(Context context, String msg) {
        toast(context, msg, LENGTH_SHORT, WARNING);
    }

    @SuppressWarnings("unused")
    public static void toastError(Context context, String msg) {
        toast(context, msg, LENGTH_SHORT, ERROR);
    }

    @SuppressWarnings("unused")
    public static void toastInfo(Context context, String msg) {
        toast(context, msg, LENGTH_SHORT, INFO);
    }

    @SuppressWarnings("unused")
    public static void toastDefault(Context context, String msg) {
        toast(context, msg, LENGTH_SHORT, DEFAULT);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    @SuppressWarnings("unused")
    public static void resetToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

    }

    @SuppressWarnings("unused")
    public static void showToast() {
        if (toast != null) {
            toast.cancel();
            toast.show();
        }
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    private static void createDefaultView(Context context, TextView text, LinearLayout cartoonViewContainer, LinearLayout.LayoutParams params) {
        DefaultToastView defaultToastView = new DefaultToastView(context);
        defaultToastView.startAnim();
        text.setBackgroundResource(R.drawable.kartoon_default_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        cartoonViewContainer.addView(defaultToastView, params);
    }

    private static void createInfoView(Context context, TextView text, LinearLayout cartoonViewContainer, LinearLayout.LayoutParams params) {
        InfoToastView infoToastView = new InfoToastView(context);
        infoToastView.startAnim();
        text.setBackgroundResource(R.drawable.kartoon_info_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        cartoonViewContainer.addView(infoToastView, params);
    }

    private static void createErrorView(Context context, TextView text, LinearLayout cartoonViewContainer, LinearLayout.LayoutParams params) {
        ErrorToastView errorToastView = new ErrorToastView(context);
        errorToastView.startAnim();
        text.setBackgroundResource(R.drawable.kartoon_error_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        cartoonViewContainer.addView(errorToastView, params);
    }

    private static void createWarningView(Context context, TextView text, LinearLayout cartoonViewContainer, LinearLayout.LayoutParams params) {
        final WarningToastView warningToastView = new WarningToastView(context);
        SpringSystem springSystem = SpringSystem.create();
        final Spring spring = springSystem.createSpring();
        spring.setCurrentValue(1.8);
        SpringConfig config = new SpringConfig(40, 5);
        spring.setSpringConfig(config);
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = (0.9f - (value * 0.5f));

                warningToastView.setScaleX(scale);
                warningToastView.setScaleY(scale);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // do nothing
                }
                spring.setEndValue(0.4f);
            }
        }).start();
        text.setBackgroundResource(R.drawable.kartoon_warning_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        cartoonViewContainer.addView(warningToastView, params);
    }

    private static void createSuccessView(Context context, TextView text, LinearLayout cartoonViewContainer, LinearLayout.LayoutParams params) {
        SuccessToastView successToastView = new SuccessToastView(context);
        successToastView.startAnim();
        text.setBackgroundResource(R.drawable.kartoon_success_toast);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        cartoonViewContainer.addView(successToastView, params);
    }


}
