// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Character;
import java.util.Arrays;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public final class ConditionPackingChar<N, T, Q> extends Condition<N, T, Character, Q> {
  public ConditionPackingChar(T target, PackingColumn<Character> column,
      CallBack<Condition<N, T, ?, Q>> callBack, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator) {
    super(target, column, callBack, builder, creator);
  }

  public Operator<N, T, Character, Q> equal(Character value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = EQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> unequal(Character value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = UNEQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> greater(Character value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> greaterEqual(Character value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = GREATER_EQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> less(Character value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> lessEqual(Character value) {
    this.value.add(value);
    this.callBack.call(this);
    this.assign = LESS_EQUAL;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> between(Character value1, Character value2) {
    this.value.add(value1);
    this.value.add(value2);
    this.callBack.call(this);
    this.assign = BETWEEN;
    return new Operator<N, T, Character, Q>(this);
  }

  public Operator<N, T, Character, Q> in(Character... value) {
    this.value.addAll(Arrays.asList(value));
    this.callBack.call(this);
    this.assign = IN;
    return new Operator<N, T, Character, Q>(this);
  }
}
