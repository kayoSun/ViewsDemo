package com.kayo.mutiadapter.rules;

import android.view.ViewGroup;

import com.kayo.mutiadapter.MutiData;
import com.kayo.mutiadapter.MutiHolder;

/**
 * Created by shilei on 17/2/4.
 * <pre>
 *
 * </pre>
 */

public interface Rule<D extends MutiData,VH extends MutiHolder<D>> {

    int layoutId();
    VH holder(ViewGroup parent,int layoutId);
    void convert(VH holder, D data);
}
