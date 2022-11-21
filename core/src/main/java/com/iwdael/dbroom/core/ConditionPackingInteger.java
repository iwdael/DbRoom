// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Integer;
import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingInteger<N, T, Q> extends Condition<N, T, Integer, Q> {
  public ConditionPackingInteger(T target, PackingColumn<Integer> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Integer, Q> equal(Integer value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> unequal(Integer value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> greater(Integer value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> greaterEqual(Integer value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> less(Integer value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> lessEqual(Integer value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> between(Integer value1, Integer value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Integer, Q>(this);
  }

  public Operator<N, T, Integer, Q> in(Integer... value) {
    this.value.addAll(Arrays.asList(value));
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Integer, Q>(this);
  }
}
