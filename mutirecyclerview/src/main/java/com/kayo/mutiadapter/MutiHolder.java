package com.kayo.mutiadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by shilei on 17/1/11.
 * <pre>
 *
 * </pre>
 */

public abstract class MutiHolder<I extends MutiData> extends RecyclerView.ViewHolder {

    public MutiHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(I data);

}
