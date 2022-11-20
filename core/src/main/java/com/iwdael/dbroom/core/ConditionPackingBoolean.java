// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Boolean;
import java.util.Arrays;

public final class ConditionPackingBoolean<N, T, Q> extends Condition<N, T, Boolean, Q> {
  public ConditionPackingBoolean(T target, PackingColumn<Boolean> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Boolean, Q> equal(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> unequal(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> greater(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> greaterEqual(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> less(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> lessEqual(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> between(Boolean value1, Boolean value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> like(Boolean value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Boolean, Q>(this);
  }

  public Operator<N, T, Boolean, Q> in(Boolean... value) {
    this.value.addAll(Arrays.asList(value));
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Boolean, Q>(this);
  }
}
