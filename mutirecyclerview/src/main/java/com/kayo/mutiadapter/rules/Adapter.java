package com.kayo.mutiadapter.rules;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.kayo.mutiadapter.MutiHolder;

import java.util.List;

/**
 * Created by shilei on 17/2/3.
 * <pre>
 *
 * </pre>
 */

public class Adapter extends RecyclerView.Adapter<MutiHolder> {

    private AdapterRulesManager manager;

    public Adapter(AdapterRulesManager manager) {
        this.manager = manager;
    }

    @Override
    public MutiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return manager.getHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(MutiHolder holder, int position) {
        manager.bindData(holder,position);
    }

    @Override
    public int getItemCount() {
        return manager.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return manager.getType(position);
    }

    @Override
    public long getItemId(int position) {
        return manager.getId(position);
    }

    @Override
    public void onViewAttachedToWindow(MutiHolder holder) {
        super.onViewAttachedToWindow(holder);
        manager.onViewAttachedToWindow(holder);
    }
}
