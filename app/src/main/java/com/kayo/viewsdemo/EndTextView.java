package com.kayo.viewsdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by shilei on 17/1/4.
 * <pre>
 *
 * </pre>
 */

public class EndTextView extends TextView {
    public EndTextView(Context context) {
        super(context);
    }

    public EndTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EndTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        width -= getPaddingLeft();
        width -= getPaddingRight();
        System.out.println("EndTextView --> " + "width = "+width);
        float v = getPaint().measureText(getText().toString());
        System.out.println("EndTextView --> " + "textWidth = "+v);
        if (width<v){
            setText("");
        }
        super.onDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
