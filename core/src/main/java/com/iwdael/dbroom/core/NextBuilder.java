// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

public interface NextBuilder<N, T, Q> {
  N build(T target, CallBack<Condition<N, T, ?, Q>> callback, NextBuilder<N, T, Q> builder,
      Creator<T, Q> creator);
}
