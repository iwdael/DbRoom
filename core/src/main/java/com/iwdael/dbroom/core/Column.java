// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.String;

public abstract class Column<T> {
  private final String name;

  public Column(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
