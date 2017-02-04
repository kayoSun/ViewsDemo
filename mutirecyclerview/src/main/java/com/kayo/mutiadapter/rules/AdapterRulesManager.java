package com.kayo.mutiadapter.rules;

import android.view.ViewGroup;

import com.kayo.mutiadapter.ColumnRule;
import com.kayo.mutiadapter.MutiData;
import com.kayo.mutiadapter.MutiHolder;
import com.kayo.mutiadapter.MutiListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shilei on 17/2/3.
 * <pre>
 *      适配器规则管理器
 * </pre>
 */

public class AdapterRulesManager<D extends MutiData> {

    private List<D> datas = new ArrayList<>();
    private Map<Integer,Rule> ruleMap = new HashMap<>();
    private MutiListView recyclerView;
    private Adapter adapter;
    private int footerType;
    private boolean hasFooter;
    private boolean hasHeader;

    public AdapterRulesManager() {
        adapter = new Adapter(this);
    }

    public AdapterRulesManager bindRecyclerView(MutiListView view){
        recyclerView = view;
        return this;
    }

    public void bindAdapter(Adapter adapter){
        this.adapter = adapter;
    }

    public AdapterRulesManager addRule(Rule rule){
        int i = rule.layoutId();
        if (!ruleMap.containsKey(i)){
            ruleMap.put(i,rule);
        }
        return this;
    }

    public void addData(D data){
        datas.add(data);
    }

    public AdapterRulesManager setDatas(List<D> datas ){
        this.datas.clear();
        this.datas.addAll(datas);
        return this;
    }

    public AdapterRulesManager addDatas(List<D> datas ){
        this.datas.addAll(datas);
        adapter.notifyDataSetChanged();
        return this;
    }

    public MutiHolder getHolder(ViewGroup parent, int viewType){
        Rule rule = ruleMap.get(viewType);
        return rule.holder(parent,rule.layoutId());
    }

    public void bindData(MutiHolder holder,int position){
        Rule rule = ruleMap.get(getType(position));
        rule.convert(holder,getItem(position));
    }

    public int getCount(){
        int count = 0;
        if (hasFooter){
            count++;
        }
        if (hasHeader){
            count++;
        }
        count+= datas.size();
        return count;
    }

    public int getType(int position){
        if (hasFooter && position == getCount()-1){
            return footerType;
        }
        return datas.get(position).getItemType();
    }

    public MutiData getItem(int position){
        if (hasFooter && position == getCount()-1){
            return null;
        }
        return datas.get(position);
    }

    public long getId(int position){
        return 0;
    }

    public void onViewAttachedToWindow(MutiHolder holder) {

    }

    public void show(){
        recyclerView.setAdapter(adapter);
    }


    public AdapterRulesManager addFooter(LoadMoreRule rule){
        hasFooter = true;
        footerType = rule.layoutId();
        recyclerView.addColumnRule(new ColumnRule(rule.layoutId(),recyclerView.getColumn()));
        addRule(rule);
        return this;
    }

    public void addHeader(){}


    public Adapter getAdapter() {
        return adapter;
    }
}
