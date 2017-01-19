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
 *
 * </pre>
 */

public class MutiListView extends RecyclerView {

    public MutiListView(Context context) {
        super(context);
    }

    public MutiListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MutiListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Deprecated
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    public void setAdapter(MutiAdapter adapter) {
        super.setAdapter(adapter);
    }

    private MutiItmHelper mutiItmHelper = new MutiItmHelper(this);

    public void setColumn(int column) {
        mutiItmHelper.setColumn(column);
    }

    public void addColumnRules(Rule rule) {
        mutiItmHelper.addColumnRules(rule);
    }

    public MutiItmHelper getMutiItmHelper() {
        return mutiItmHelper;
    }

    public int getColumn() {
        return mutiItmHelper.getColumn();
    }

    private class MutiItmHelper {

        private List<Rule> rules;
        private SparseIntArray rulesInt;
        private MutiListView parent;
        private RecyclerView.LayoutManager layoutManager;
        private int column = 1;

        private MutiItmHelper(MutiListView parent) {
            this.parent = parent;
        }

        public void setParent(MutiListView parent) {
            this.parent = parent;
            setColumn(column);
        }

        private void addColumnRules(Rule rule) {
            if (null == rules) {
                rules = new ArrayList<>();
            }
            rules.add(rule);
            if (null == rulesInt) {
                rulesInt = new SparseIntArray();
            }
            rulesInt.put(rule.getType(), rule.getRule());
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
