package com.kayo.mutiadapter.rules;

import com.kayo.mutiadapter.MutiData;
import com.kayo.mutiadapter.MutiHolder;

/**
 * Created by shilei on 17/2/4.
 * <pre>
 *
 * </pre>
 */

public abstract class AbsRule<D extends MutiData,VH extends MutiHolder<D>> implements Rule<D,VH> {

    int layoutId;

    public AbsRule(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public int layoutId() {
        return layoutId;
    }
}
