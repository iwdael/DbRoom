// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.String;

public final class Operator<N, T, R, Q> {
  protected static final String AND = "AND";

  protected static final String OR = "OR";

  protected final Condition<N, T, R, Q> where;

  protected String operator = null;

  protected Operator(Condition<N, T, R, Q> where) {
    this.where = where;
  }

  public final N and() {
    operator = AND;
    where.next = this;
    return where.builder.build(where.target, where.callBack, where.builder, where.creator);
  }

  public final N or() {
    operator = OR;
    where.next = this;
    return where.builder.build(where.target, where.callBack, where.builder, where.creator);
  }

  public Q build() {
    return where.creator.create(where.target);
  }
}
