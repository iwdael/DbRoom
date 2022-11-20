// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Boolean;

public final class ConditionBasicBoolean<N, T, Q> extends Condition<N, T, Boolean, Q> {
  public ConditionBasicBoolean(T target, BasicColumn<Boolean> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
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

  public Operator<N, T, Boolean, Q> greater(boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> greaterEqual(boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> less(boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> lessEqual(boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> between(boolean value1, boolean value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> like(boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> in(boolean... values) {
    for (boolean value : values) {
      this.value.add(value);
    }
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Boolean, Q>(this);
  }
}
