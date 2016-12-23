package com.kayo.mutiimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by shilei on 16/11/1.
 * <pre>
 *      圆角ImageView
 *   用时 只需要设置
 *      app:style="rectangle" 矩形  circle圆形
 *      app:top_left="30dp"       左上圆角半径
 *      app:top_right="10dp"      右上圆角半径
 *      app:bottom_left="10dp"    左下圆角半径
 *      app:bottom_right="30dp"   右上圆角半径
 * </pre>
 */
public class MutiImageView extends ImageView {
    public final int STYLE_CIRCLE = 0;//圆形
    public final int STYLE_RECTANGLE = 1;//矩形
    private int topLeft;
    private int bottomLeft;
    private int topRight;
    private int bottomRight;

    private int style;//控件样式

    private PathHelper helper;
    private Paint paint2;

    public MutiImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public MutiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MutiImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, com.kayo.mutiimageview.R.styleable.MutiImageView);
        if (null != a) {
            topLeft = (int) a.getDimension(com.kayo.mutiimageview.R.styleable.MutiImageView_top_left, 0);
            topRight = (int) a.getDimension(com.kayo.mutiimageview.R.styleable.MutiImageView_top_right, 0);
            bottomLeft = (int) a.getDimension(com.kayo.mutiimageview.R.styleable.MutiImageView_bottom_left, 0);
            bottomRight = (int) a.getDimension(com.kayo.mutiimageview.R.styleable.MutiImageView_bottom_right, 0);
            style = a.getInt(com.kayo.mutiimageview.R.styleable.MutiImageView_style, STYLE_RECTANGLE);
            a.recycle();
        }

        helper = new PathHelper(this);
        helper.setStyle(style);
        helper.setRound(topLeft,bottomLeft,topRight,bottomRight);

        paint2 = new Paint();
        paint2.setXfermode(null);

    }

    public int getStyle() {
        return style;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        helper.draw(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();
    }

}
