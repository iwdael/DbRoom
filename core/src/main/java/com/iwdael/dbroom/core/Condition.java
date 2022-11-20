// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public abstract class Condition<N, T, R, Q> {
  protected static final String EQUAL = "=";

  protected static final String UNEQUAL = "<>";

  protected static final String GREATER = ">";

  protected static final String GREATER_EQUAL = ">=";

  protected static final String LESS = "<";

  protected static final String LESS_EQUAL = "<=";

  protected static final String BETWEEN = "BETWEEN";

  protected static final String LIKE = "LIKE";

  protected static final String IN = "IN";

  protected final T target;

  protected final CallBack<Condition<N, T, ?, Q>> callBack;

  protected final Creator<T, Q> creator;

  protected final Column<R> column;

  protected final List<R> value = new ArrayList<>();

  protected final NextBuilder<N, T, Q> builder;

  protected String assign = null;

  protected Operator<N, T, R, Q> next = null;

  public Condition(T target, Column<R> column, CallBack<Condition<N, T, ?, Q>> callBack,
      NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
    this.column = column;
    this.target = target;
    this.callBack = callBack;
    this.builder = builder;
    this.creator = creator;
  }
}
