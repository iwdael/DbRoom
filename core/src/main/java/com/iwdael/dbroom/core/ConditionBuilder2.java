// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public abstract class ConditionBuilder2<N, T, Q> {
  protected final T target;

  protected final CallBack<Condition<N, T, ?, Q>> callBack;

  protected final Creator<T, Q> creator;

  protected final NextBuilder<N, T, Q> builder;

  public ConditionBuilder2(T target, CallBack<Condition<N, T, ?, Q>> callBack,
      NextBuilder<N, T, Q> builder, Creator<T, Q> creator) {
    this.target = target;
    this.callBack = callBack;
    this.builder = builder;
    this.creator = creator;
  }
}
