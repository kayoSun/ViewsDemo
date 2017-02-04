package com.kayo.mutiadapter.installer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kayo on 2017/1/31.
 * 数据页面布局创建者
 * 由于创建UI
 */

public class UIBuilder {
    Context context;
    int layoutID;
    ViewGroup viewGroup;
    private View view;

    public UIBuilder(Context context, int layoutID) {
        this(context,layoutID,null);
    }

    public UIBuilder(Context context, int layoutID, ViewGroup viewGroup) {
        this.context = context;
        this.layoutID = layoutID;
        this.viewGroup = viewGroup;

        init();
    }

    private void init(){
        view = LayoutInflater.from(context).inflate(layoutID,viewGroup,false);
    }

    public View getView(){
        return view;
    }
}
