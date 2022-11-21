// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.String;
import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingString<N, T, Q> extends Condition<N, T, String, Q> {
  public ConditionPackingString(T target, PackingColumn<String> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, String, Q> equal(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> unequal(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> greater(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> greaterEqual(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> less(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> lessEqual(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> between(String value1, String value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> like(String value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, String, Q>(this);
  }

  public Operator<N, T, String, Q> in(String... value) {
    this.value.addAll(Arrays.asList(value));
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, String, Q>(this);
  }
}
