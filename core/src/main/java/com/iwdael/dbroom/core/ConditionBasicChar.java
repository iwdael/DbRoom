// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Character;

public final class ConditionBasicChar<N, T, Q> extends Condition<N, T, Character, Q> {
  public ConditionBasicChar(T target, BasicColumn<Character> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Character, Q> equal(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> unequal(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> greater(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> greaterEqual(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> less(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> lessEqual(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> between(char value1, char value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> like(char value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> in(char... values) {
    for (char value : values) {
      this.value.add(value);
    }
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Character, Q>(this);
  }
}
