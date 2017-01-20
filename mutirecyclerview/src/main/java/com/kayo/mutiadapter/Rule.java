package com.kayo.mutiadapter;

/**
 * Created by shilei on 17/1/11.
 * <pre>
 *  列表展示规则
 * </pre>
 */

public class Rule {
    int type;//表示type，条目类型的位置ID 可用布局文件ID作为此值
    int rule;//表示占行数 或者 列数

    public Rule(){}
    public Rule(int type, int rule) {
        this.type = type;
        this.rule = rule;
    }

    public int getRule() {
        return rule;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }
}
