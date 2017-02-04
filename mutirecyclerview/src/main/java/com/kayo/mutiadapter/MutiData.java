package com.kayo.mutiadapter;

import android.support.annotation.LayoutRes;

/**
 * Created by shilei on 17/1/11.
 * <pre>
 *
 * </pre>
 */

public class MutiData implements IMutiData{
    /**
     * 指定 条目数据类型 一般指定为相应的布局文件ID
     */
    @LayoutRes
    private int itemType;
    private long itemId;

    @LayoutRes
    public int getItemType() {
        return itemType;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemType(@LayoutRes int itemType) {
        this.itemType = itemType;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
}
