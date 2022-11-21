package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public abstract class ConditionBuilder<N, T, Q> {
    protected final T target;

    protected final CallBack<Condition<N, T, ?, Q>> call;

    protected final Creator<T, Q> creator;

    protected final NextBuilder<N, T, Q> builder;

    protected ConditionBuilder(T target, CallBack<Condition<N, T, ?, Q>> call, NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
        this.target = target;
        this.call = call;
        this.builder = builder;
        this.creator = creator;
    }
}
