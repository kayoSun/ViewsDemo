package com.kayo.mutiadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilei on 17/1/11.
 * <pre>
 *      recyclerview
 * </pre>
 */

public class MutiListView extends RecyclerView {

    private MutiItmHelper mutiItmHelper;

    public MutiListView(Context context) {
        this(context,null);
    }

    public MutiListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public MutiListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mutiItmHelper = new MutiItmHelper(this);
    }

    @Deprecated
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    public void setAdapter(MutiAdapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * 设置条目的最大展示 列数或行数
     * @param column 列数或行数
     */
    public void setColumn(int column) {
        mutiItmHelper.setColumn(column);
    }

    /**
     * 条件调试展示规则
     * @param columnRule  规则
     */
    public void addColumnRule(ColumnRule columnRule) {
        mutiItmHelper.addColumnRule(columnRule);
    }

    public MutiItmHelper getMutiItmHelper() {
        return mutiItmHelper;
    }

    public int getColumn() {
        return mutiItmHelper.getColumn();
    }

    //MutiListView 条目 帮助类
    public static class MutiItmHelper {

        private List<ColumnRule> columnRules;
        private SparseIntArray rulesInt;
        private MutiListView parent;
        private RecyclerView.LayoutManager layoutManager;
        private int column = 1;

        private MutiItmHelper(MutiListView parent) {
            this.parent = parent;
        }

        public void bindParent(MutiListView parent) {
            this.parent = parent;
            setColumn(column);
        }

        private void addColumnRule(ColumnRule columnRule) {
            if (null == columnRule){
                return;
            }
            if (column < columnRule.getRule()){
                column = columnRule.getRule();
                setColumn(column);
            }
            if (null == columnRules) {
                columnRules = new ArrayList<>();
            }
            columnRules.add(columnRule);
            if (null == rulesInt) {
                rulesInt = new SparseIntArray();
            }
            rulesInt.put(columnRule.getType(), columnRule.getRule());
        }

        private void setColumn(int column) {
            this.column = column;
            if (parent == null){
                return;
            }
            if (column <= 1) {
                layoutManager = new LinearLayoutManager(parent.getContext());
            } else {
                layoutManager = new GridLayoutManager(parent.getContext(), column);
                lookAtItem((GridLayoutManager) layoutManager);
            }
            parent.setLayoutManager(layoutManager);
        }

        private void lookAtItem(GridLayoutManager manager) {
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                          @Override
                                          public int getSpanSize(int position) {
                                              if (null == rulesInt){
                                                  return 1;
                                              }
                                              if (null == parent){
                                                  return 1;
                                              }
                                              if (null == parent.getAdapter()){
                                                  return 1;
                                              }
                                              int itemViewType = parent.getAdapter().getItemViewType(position);
                                              return rulesInt.get(itemViewType);
                                          }
                                      }
            );
        }

        private int getColumn() {
            return column;
        }
    }


}
