package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionBasicDouble<N, T, Q> extends Condition<N, T, Double, Q> {
    public ConditionBasicDouble(T target, BasicColumn<Double> column, CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
        super(target, column, callBack, builder, creator);
    }

    public Operator<N, T, Double, Q> equal(double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = EQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> unequal(double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = UNEQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> greater(double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> greaterEqual(double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER_EQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> less(double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> lessEqual(double value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS_EQUAL;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> between(double value1, double value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.callBack.call(this);
        this.assign = BETWEEN;
        return new Operator<N, T, Double, Q>(this);
    }

    public Operator<N, T, Double, Q> in(double... values) {
        for (double value : values) {
            this.value.add(value);
        }
        this.callBack.call(this);
        this.assign = IN;
        return new Operator<N, T, Double, Q>(this);
    }
}
