package com.iwdael.dbroom.core;

import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingDouble<N, T, Q> extends Condition<N, T, Double, Q> {
    public ConditionPackingDouble(T target, PackingColumn<Double> column, CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
        super(target, column, callBack, builder, creator);
    }

    public Operator<N, T, Double, Q> equal(Double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = EQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> unequal(Double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = UNEQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> greater(Double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> greaterEqual(Double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER_EQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> less(Double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> lessEqual(Double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS_EQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> between(Double value1, Double value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.callBack.call(this);
        this.assign = BETWEEN;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> in(Double... value) {
        this.value.addAll(Arrays.asList(value));
        this.callBack.call(this);
        this.assign = IN;
        return new Operator<N, T, Double, Q>(this);
    }
}
