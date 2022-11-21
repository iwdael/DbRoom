package com.iwdael.dbroom.core;

import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingByte<N, T, Q> extends Condition<N, T, Byte, Q> {
    public ConditionPackingByte(T target, PackingColumn<Byte> column, CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
        super(target, column, callBack, builder, creator);
    }

    public Operator<N, T, Byte, Q> equal(Byte value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = EQUAL;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> unequal(Byte value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = UNEQUAL;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> greater(Byte value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> greaterEqual(Byte value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = GREATER_EQUAL;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> less(Byte value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> lessEqual(Byte value) {
        this.value.add(value);
        this.callBack.call(this);
        this.assign = LESS_EQUAL;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> between(Byte value1, Byte value2) {
        this.value.add(value1);
        this.value.add(value2);
        this.callBack.call(this);
        this.assign = BETWEEN;
        return new Operator<N, T, Byte, Q>(this);
    }

    public Operator<N, T, Byte, Q> in(Byte... value) {
        this.value.addAll(Arrays.asList(value));
        this.callBack.call(this);
        this.assign = IN;
        return new Operator<N, T, Byte, Q>(this);
    }
}
