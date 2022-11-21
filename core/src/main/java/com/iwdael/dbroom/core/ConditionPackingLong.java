package com.iwdael.dbroom.core;

import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingLong<N, T, Q> extends Condition<N, T, Long, Q> {
    public ConditionPackingLong(T target, PackingColumn<Long> column, CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
        super(target, column, callBack, builder, creator);
    }

    public Operator<N, T, Long, Q> equal(Long value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = EQUAL;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> unequal(Long value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = UNEQUAL;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> greater(Long value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> greaterEqual(Long value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER_EQUAL;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> less(Long value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> lessEqual(Long value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS_EQUAL;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> between(Long value1, Long value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.callBack.call(this);
        this.assign = BETWEEN;
        return new Operator<N, T, Long, Q>(this);
    }

    public Operator<N, T, Long, Q> in(Long... value) {
        this.value.addAll(Arrays.asList(value));
        this.callBack.call(this);
        this.assign = IN;
        return new Operator<N, T, Long, Q>(this);
    }
}
