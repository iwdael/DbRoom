// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Byte;

public final class ConditionBasicByte<N, T, Q> extends Condition<N, T, Byte, Q> {
  public ConditionBasicByte(T target, BasicColumn<Byte> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Byte, Q> equal(byte value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> unequal(byte value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> greater(byte value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> greaterEqual(byte value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> less(byte value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> lessEqual(byte value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> between(byte value1, byte value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Byte, Q>(this);
  }

  public Operator<N, T, Byte, Q> in(byte... values) {
    for (byte value : values) {
      this.value.add(value);
    }
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Byte, Q>(this);
  }
}
