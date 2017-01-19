package com.kayo.mutiadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kayo.mutiadapter.footer.FooterHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilei on 17/1/11.
 * <pre>
 *
 * </pre>
 */

public abstract class MutiAdapter<I extends MutiData> extends RecyclerView.Adapter<MutiHolder<I>> {

    private List<I> dataList;
    private LayoutInflater inflater;
    private boolean hasFooter;//是否有 脚布局
    private FooterHolder footerHolder;

    public MutiAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MutiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateMutiHolder(inflater,parent,viewType);
    }

    @Override
    public void onBindViewHolder(MutiHolder holder, int position) {
        onBindMutiData(holder,getItemData(position),position);
    }

    @Override
    public int getItemCount() {
        if (dataList == null){
            return 0;
        }
        return dataList.size();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public long getItemId(int position) {
        if (getItemData(position) == null){
            return 0;
        }
        return getItemData(position).getItemId();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList == null){
            return 0;
        }
        if (dataList.size() < position-1){
            return 0;
        }
        return dataList.get(position).getItemType();
    }

    public abstract MutiHolder onCreateMutiHolder(LayoutInflater inflater,ViewGroup parent, int viewType);
    public abstract void onBindMutiData(MutiHolder holder,I data,int position);

    private I getItemData(int position){
        if (dataList == null){
            return null;
        }
        if (dataList.size() < position-1){
            return null;
        }
        return dataList.get(position);
    }

    public void setData(List<I> list){
        if (null == list || list.size()==0){
            return;
        }
        if (null == dataList){
            dataList = new ArrayList<>();
        }else {
            dataList.clear();
        }
        addData(list);
    }

    public void addData(List<I> list){
        if (null == list || list.size()==0){
            return;
        }
        if (null == dataList){
            dataList = new ArrayList<>();
        }
        dataList.addAll(list);
    }



}
