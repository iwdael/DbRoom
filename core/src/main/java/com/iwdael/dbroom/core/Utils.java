// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import java.lang.Object;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {
  protected static final String SELECT = "SELECT";

  protected static final String WHERE = "WHERE";

  protected static final String FROM = "FROM";

  protected static final String SPACE = " ";

  protected static final String AND = "AND";

  public static String toQuerySelection(String tableName, Object[] columns,
      List<Condition<?, ?, ?, ?>> wheres) {
    StringBuilder builder = new StringBuilder();
    builder.append(SELECT).append(SPACE);
    for (Object column : columns) {
      builder.append(column).append(SPACE);
    }
    if (columns.length == 0) {
      builder.append("*").append(SPACE);
    }
    builder.append(FROM).append(SPACE).append(tableName).append(SPACE);
    if (wheres.isEmpty()) {
      return builder.toString();
    } else {
      builder.append(toWhere(wheres));
      return builder.toString();
    }
  }

  public static Object[] toBindArgs(List<Condition<?, ?, ?, ?>> wheres) {
    List<Object> bindArgs = new ArrayList<>();
    for (Condition<?, ?, ?, ?> where : wheres) {
      bindArgs.addAll(where.value);
    }
    return bindArgs.toArray();
  }

  private static String toWhere(List<Condition<?, ?, ?, ?>> conditions) {
    StringBuilder builder = new StringBuilder();
    if(!conditions.isEmpty()) {
      builder.append(WHERE).append(SPACE);
    }
    for (Condition<?, ?, ?, ?> condition : conditions) {
      if (Objects.equals(condition.assign, Condition.BETWEEN)) {
        builder.append(condition.column).append(SPACE).append(condition.assign).append(SPACE).append('?').append(SPACE).append(AND).append(SPACE).append('?').append(SPACE);
      } else if (Objects.equals(condition.assign, Condition.IN)) {
        builder.append(condition.column).append(SPACE).append(condition.assign).append(SPACE).append('(');
        int count = condition.value.size();
        for (int index = 0; index < count; index++) {
          builder.append("?");
          if (index != count - 1) {
            builder.append(SPACE).append(",").append(SPACE);
          }
        }
        builder.append(')').append(SPACE);
      } else {
        builder.append(condition.column).append(SPACE).append(condition.assign).append(SPACE).append('?').append(SPACE);
      }
      if (condition.next != null) {
        builder.append(condition.next.operator).append(SPACE);
      }
    }
    return builder.toString();
  }

  public static <C, T> List<T> collectionConvert(List<C> sources, Creator<C, T> creator) {
    List<T> collection = new ArrayList<>(sources.size());
    for (C source : sources) {
      collection.add(creator.create(source));
    }
    return collection;
  }
}
