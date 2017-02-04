package com.kayo.mutiadapter.installer;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kayo on 2017/1/31.
 */

public abstract class UIHolder<D> extends RecyclerView.ViewHolder {
    public UIHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(D data);
}
