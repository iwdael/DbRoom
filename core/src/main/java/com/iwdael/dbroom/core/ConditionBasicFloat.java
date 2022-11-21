// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Float;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionBasicFloat<N, T, Q> extends Condition<N, T, Float, Q> {
  public ConditionBasicFloat(T target, BasicColumn<Float> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Float, Q> equal(float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> unequal(float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> greater(float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> greaterEqual(float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> less(float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> lessEqual(float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> between(float value1, float value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> in(float... values) {
    for (float value : values) {
      this.value.add(value);
    }
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Float, Q>(this);
  }
}
