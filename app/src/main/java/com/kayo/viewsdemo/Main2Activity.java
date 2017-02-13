package com.kayo.viewsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kayo.cartoontoast.KartoonBuilder;
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
//        KartoonBuilder.builder().context(this).message("WARNING").type(KartoonToast.WARNING).duration(KartoonToast.LENGTH_SHORT).show();
//        KartoonBuilder.builder().context(this).message("SUCCESS").type(KartoonToast.SUCCESS).duration(KartoonToast.LENGTH_SHORT).show();
//        KartoonBuilder.builder().context(this).message("ERROR").type(KartoonToast.ERROR).duration(KartoonToast.LENGTH_SHORT).show();
//        KartoonBuilder.builder().context(this).message("INFO").type(KartoonToast.INFO).duration(KartoonToast.LENGTH_SHORT).show();
//        KartoonBuilder.builder().context(this).message("DEFAULT").type(KartoonToast.DEFAULT).duration(KartoonToast.LENGTH_SHORT).show();
    }
}
