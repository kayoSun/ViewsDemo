package com.kayo.motionlayout;

/**
 * Created by shilei on 16/12/22.
 * <pre>
 *  下拉刷新监听
 * </pre>
 */

public interface IRefreshListener {

    void onRefresh();

    void onPullDistance(int distance);

    void onPullEnable(boolean enable);
}
