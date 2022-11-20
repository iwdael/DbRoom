// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Short;

public final class ConditionBasicShort<N, T, Q> extends Condition<N, T, Short, Q> {
  public ConditionBasicShort(T target, BasicColumn<Short> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Short, Q> equal(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> unequal(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> greater(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> greaterEqual(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> less(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> lessEqual(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> between(short value1, short value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> like(short value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Short, Q>(this);
  }

  public Operator<N, T, Short, Q> in(short... values) {
    for (short value : values) {
      this.value.add(value);
    }
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Short, Q>(this);
  }
}
