// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Long;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionBasicLong<N, T, Q> extends Condition<N, T, Long, Q> {
  public ConditionBasicLong(T target, BasicColumn<Long> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Long, Q> equal(long value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> unequal(long value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> greater(long value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> greaterEqual(long value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> less(long value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> lessEqual(long value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> between(long value1, long value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Long, Q>(this);
  }

  public Operator<N, T, Long, Q> in(long... values) {
    for (long value : values) {
      this.value.add(value);
    }
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Long, Q>(this);
  }
}
