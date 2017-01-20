package com.kayo.animators;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

import com.kayo.animators.adapters.AnimationAdapter;
import com.kayo.animators.animators.BaseItemAnimator;
import com.kayo.animators.interfaces.IAnimeSetting;

/**
 * Created by shilei on 17/1/20.
 * <pre>
 *      RecyclerView 条目及列表动画帮助类
 * </pre>
 */

public class RecyclerViewHelper {

    @SuppressLint("StaticFieldLeak")
    private static RecyclerViewHelper instance;
    private RecyclerViewHelper() {
    }

    public static RecyclerViewHelper getHelper() {
        if (instance == null) {
            synchronized (RecyclerViewHelper.class) {
                if (instance == null) {
                    instance = new RecyclerViewHelper();
                }
            }
        }
        return instance;
    }

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;
    private BaseItemAnimator itemAnimator;
    private Interpolator itemInterpolator;
    private int itemDuration = 100;
    private AnimationAdapter animationAdapter;
    private Interpolator adapterInterpolator;
    private IAnimeSetting animeSetting;
    private int adapterDuration= 100;

    /**
     * 绑定RecyclerView
     * @param recyclerView 备操作的recyclerview
     */
    public RecyclerViewHelper bindRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    /**
     * 绑定原生数据适配器
     * @param adapter 数据适配器
     */
    public RecyclerViewHelper bindDataAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        return this;
    }

    /**
     * 绑定条目动画
     * @param itemAnimator 条目动画
     */
    public RecyclerViewHelper bindItemAnimatior(BaseItemAnimator itemAnimator) {
        this.itemAnimator = itemAnimator;
        notifyItemChanged();
        return this;
    }

    /**
     * 绑定条目动画 差值器
     * @param itemInterpolator 差值器
     */
    public RecyclerViewHelper bindItemInterpolator(Interpolator itemInterpolator) {
        this.itemInterpolator = itemInterpolator;
        notifyItemChanged();
        return this;
    }

    /**
     * 设置条目动画执行时间
     * @param itemDuration 动画时间
     */
    public RecyclerViewHelper setItemDuration(int itemDuration) {
        this.itemDuration = itemDuration;
        notifyItemChanged();
        return this;
    }

    /**
     * 绑定列表动画数据适配器
     * @param adapter  动画适配器
     */
    public RecyclerViewHelper bindAnimationAdapter(AnimationAdapter adapter) {
        this.animationAdapter = adapter;
        notifyAdapterChanged();
        return this;
    }

    /**
     * 绑定列表动画差值器
     * @param adapterInterpolator 动画差值器
     */
    public RecyclerViewHelper bindAdapterInterpolator(Interpolator adapterInterpolator) {
        this.adapterInterpolator = adapterInterpolator;
        notifyAdapterChanged();
        return this;
    }

    /**
     * 设置列表动画执行时间
     * @param adapterDuration 动画执行时间
     */
    public RecyclerViewHelper setAdapterDuration(int adapterDuration) {
        this.adapterDuration = adapterDuration;
        notifyAdapterChanged();
        return this;
    }

    /**
     * 绑定列表动画设置类
     * @param animeSetting 列表数据动画设置类
     */
    public RecyclerViewHelper bindAdapterAnimSetting(IAnimeSetting animeSetting){
        this.animeSetting = animeSetting;
        notifyAdapterChanged();
        return this;
    }

    //通知条目动画数据更改
    private void notifyItemChanged() {
        if (recyclerView == null) {
            return;
        }
        if (itemAnimator == null) {
            return;
        }
        this.recyclerView.setItemAnimator(this.itemAnimator);
        if (itemDuration >= 0) {
            this.itemAnimator.setAddDuration(this.itemDuration);
            this.itemAnimator.setRemoveDuration(this.itemDuration);
            this.itemAnimator.setMoveDuration(this.itemDuration);
            this.itemAnimator.setChangeDuration(this.itemDuration);
        }
        if (itemInterpolator != null) {
            this.itemAnimator.setInterpolator(this.itemInterpolator);
        }
    }

    //通知适配器动画数据更改
    private void notifyAdapterChanged() {
        if (recyclerView == null) {
            return;
        }
        if (animationAdapter == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        animationAdapter.bindAdapter(adapter);
        if (null != animeSetting){
            int duration = animeSetting.getDuration();
            if (duration>=0){
                adapterDuration = duration;
            }
            Interpolator interpolator = animeSetting.getInterpolator();
            if (null != interpolator){
                adapterInterpolator = interpolator;
            }
            animationAdapter.showFirstOnly(animeSetting.showFirstOnly());
            animationAdapter.setStartPosition(animeSetting.getStartPosition());
        }
        if (adapterInterpolator != null) {
            animationAdapter.setInterpolator(adapterInterpolator);
        }
        if (adapterDuration >= 0) {
            animationAdapter.setDuration(adapterDuration);
        }

    }

    /**
     * 展示普通列表（没有列表动画）
     */
    public void showNormalData() {
        if (recyclerView == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        recyclerView.setAdapter(adapter);
    }

    /**
     * 展示动画列表数据，如果设置了列表动画适配器
     */
    public void showAnimData() {
        if (recyclerView == null) {
            return;
        }
        if (adapter == null) {
            return;
        }
        if (animationAdapter == null) {
            return;
        }
        recyclerView.setAdapter(animationAdapter);
    }

    /**
     * 展示数据，自动判断是否展示动画列表数据
     */
    public void showData() {
        if (animationAdapter == null) {
            showNormalData();
        } else {
            showAnimData();
        }
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public RecyclerView.Adapter getDataAdapter(){
        return adapter;
    }

    public BaseItemAnimator getItemAnimator(){
        return itemAnimator;
    }

    public AnimationAdapter getAnimationAdapter(){
        return animationAdapter;
    }

    public Interpolator getItemInterpolator(){
        return itemInterpolator;
    }

    public Interpolator getAdapterInterpolator(){
        return adapterInterpolator;
    }

    /**
     * 重置帮助者中的所有数据
     */
    public void resetHelper(){
        recyclerView = null;
        adapter = null;
        itemAnimator = null;
        itemInterpolator = null;
        itemDuration = 100;
        animationAdapter = null;
        adapterInterpolator = null;
        adapterDuration =  100;
    }

}
