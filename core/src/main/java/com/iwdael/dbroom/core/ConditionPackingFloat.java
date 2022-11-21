// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Float;
import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingFloat<N, T, Q> extends Condition<N, T, Float, Q> {
  public ConditionPackingFloat(T target, PackingColumn<Float> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Float, Q> equal(Float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> unequal(Float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> greater(Float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> greaterEqual(Float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> less(Float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> lessEqual(Float value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> between(Float value1, Float value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Float, Q>(this);
  }

  public Operator<N, T, Float, Q> in(Float... value) {
    this.value.addAll(Arrays.asList(value));
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Float, Q>(this);
  }
}
