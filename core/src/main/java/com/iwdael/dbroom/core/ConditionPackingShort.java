// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Short;
import java.util.Arrays;

public final class ConditionPackingShort<N, T, Q> extends Condition<N, T, Short, Q> {
  public ConditionPackingShort(T target, PackingColumn<Short> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Short, Q> equal(Short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> unequal(Short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> greater(Short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> greaterEqual(Short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> less(Short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> lessEqual(Short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> between(Short value1, Short value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> in(Short... value) {
    this.value.addAll(Arrays.asList(value));
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Short, Q>(this);
  }
}
