package com.kayo.viewsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kayo.cartoontoast.KartoonToast;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        view = findViewById(R.id.text);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        KartoonToast.toast(this,"测试文案", KartoonToast.LENGTH_LONG,KartoonToast.WARNING);
    }
}
