package com.kayo.mutiimageview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Created by shilei on 16/12/23.
 * <pre>
 *      路径帮助类
 * </pre>
 */
class PathHelper {
    private Paint paint;
    private Path path;
    private MutiImageView view;
    private int style;

    private int topLeft;
    private int bottomLeft;
    private int topRight;
    private int bottomRight;

    PathHelper(MutiImageView view) {
        this.view = view;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);//抗锯齿
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//模式 去除多余部分 用于画背景
        path = new Path();
        path.reset();
    }

    /**
     * 设置样式
     * @param style 设置控件样式
     */
    public void setStyle(int style) {
        this.style = style;
    }

    /**
     * 设置圆角半径
     * @param leftTop 左上
     * @param leftBottom 左下
     * @param rightTop 右上
     * @param rightBottom 右下
     */
    void setRound(int leftTop, int leftBottom, int rightTop, int rightBottom) {
        topLeft = leftTop;
        topRight = rightTop;
        bottomLeft = leftBottom;
        bottomRight = rightBottom;
    }

    /**
     * 画画
     * @param canvas 相关画布
     */
    void draw(Canvas canvas) {
        if (null == view || null == path
                || null == paint || null == canvas) {
            return;
        }
        drawLiftTop(canvas);
        drawLiftBottom(canvas);
        drawRightTop(canvas);
        drawRightBottom(canvas);
    }

    private void drawLiftTop(Canvas canvas) {
        path.reset();
        if (style == view.STYLE_RECTANGLE) {
            path.moveTo(0, topLeft);
            path.lineTo(0, 0);
            path.lineTo(topLeft, 0);
            path.arcTo(new RectF(
                            0,
                            0,
                            topLeft * 2,
                            topLeft * 2),
                    -90,
                    -90);
        } else if (style == view.STYLE_CIRCLE) {
            path.moveTo(0, view.getHeight() / 2);
            path.lineTo(0, 0);
            path.lineTo(view.getWidth() / 2, 0);
            path.arcTo(new RectF(
                            0,
                            0,
                            view.getWidth(),
                            view.getHeight()),
                    -90,
                    -90);
        }
        path.close();
        canvas.drawPath(path, paint);

    }

    private void drawLiftBottom(Canvas canvas) {
        path.reset();
        if (style == view.STYLE_RECTANGLE) {
            path.moveTo(0, view.getHeight() - bottomLeft);
            path.lineTo(0, view.getHeight());
            path.lineTo(bottomLeft, view.getHeight());
            path.arcTo(new RectF(
                            0,
                            view.getHeight() - bottomLeft * 2,
                            bottomLeft * 2,
                            view.getHeight()),
                    90,
                    90);
        } else if (style == view.STYLE_CIRCLE) {
            path.moveTo(0, view.getHeight() / 2);
            path.lineTo(0, view.getHeight());
            path.lineTo(view.getWidth() / 2, view.getHeight());
            path.arcTo(new RectF(
                            0,
                            0,
                            view.getWidth(),
                            view.getHeight()),
                    90,
                    90);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightBottom(Canvas canvas) {
        path.reset();
        if (style == view.STYLE_RECTANGLE) {
            path.moveTo(view.getWidth() - bottomRight, view.getHeight());
            path.lineTo(view.getWidth(), view.getHeight());
            path.lineTo(view.getWidth(), view.getHeight() - bottomRight);
            path.arcTo(new RectF(
                    view.getWidth() - bottomRight * 2,
                    view.getHeight() - bottomRight * 2,
                    view.getWidth(),
                    view.getHeight()), 0, 90);
        } else if (style == view.STYLE_CIRCLE) {

            path.moveTo(view.getWidth() / 2, view.getHeight());
            path.lineTo(view.getWidth(), view.getHeight());
            path.lineTo(view.getWidth(), view.getHeight() / 2);
            path.arcTo(new RectF(
                    0,
                    0,
                    view.getWidth(),
                    view.getHeight()), 0, 90);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightTop(Canvas canvas) {
        path.reset();
        if (style == view.STYLE_RECTANGLE) {
            path.moveTo(view.getWidth(), topRight);
            path.lineTo(view.getWidth(), 0);
            path.lineTo(view.getWidth() - topRight, 0);
            path.arcTo(new RectF(
                            view.getWidth() - topRight * 2,
                            0,
                            view.getWidth(),
                            topRight * 2),
                    -90,
                    90);
        } else if (style == view.STYLE_CIRCLE) {
            path.moveTo(view.getWidth(), view.getHeight() / 2);
            path.lineTo(view.getWidth(), 0);
            path.lineTo(view.getWidth() / 2, 0);
            path.arcTo(new RectF(
                            0,
                            0,
                            view.getWidth(),
                            view.getHeight()),
                    -90,
                    90);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

}
