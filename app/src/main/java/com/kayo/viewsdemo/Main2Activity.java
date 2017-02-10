package com.kayo.viewsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    int actionLimitY = 100;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        view = findViewById(R.id.text);
    }

    private void drag(){

    }

//    float startX;
//    float startY;
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                startX = event.getX();
//                startY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float y = event.getY();
//                float offset = y - startY;
//                float abs = Math.abs(offset);
//                if (abs >actionLimitY){
//                    abs = actionLimitY;
//                    if (offset>0){
//                        offset = abs;
//                    }else {
//                        offset = -abs;
//                    }
//                }
//                moveView(offset);
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    private void moveView(float offset){
//        System.out.println("Main2Activity --> " + "offset = "+offset);
//        view.bringToFront();
//        view.offsetTopAndBottom((int) offset);//设置偏移量
//        view.invalidate();
//    }
}
