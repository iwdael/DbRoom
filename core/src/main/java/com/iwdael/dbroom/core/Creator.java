// Create by https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
public interface Creator<C, T> {
  T create(C call);
}
