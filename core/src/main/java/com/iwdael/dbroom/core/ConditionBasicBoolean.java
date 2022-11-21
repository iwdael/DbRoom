package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionBasicBoolean<N, T, Q> extends Condition<N, T, Boolean, Q> {
    public ConditionBasicBoolean(T target, BasicColumn<Boolean> column, CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
        super(target, column, callBack, builder, creator);
    }

    public Operator<N, T, Boolean, Q> equal(boolean value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = EQUAL;
        return new Operator<N, T, Boolean, Q>(this);
    }

    public Operator<N, T, Boolean, Q> unequal(boolean value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = UNEQUAL;
        return new Operator<N, T, Boolean, Q>(this);
    }
}
