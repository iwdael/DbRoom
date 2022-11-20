// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.core;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.lang.String;
import org.jetbrains.annotations.NotNull;

@Entity(
    tableName = "db_store"
)
public class Store {
  @PrimaryKey
  @ColumnInfo(
      name = "store_name"
  )
  @NotNull
  public String name;

  @ColumnInfo(
      name = "store_value"
  )
  public String value;

  public Store(@NonNull String name, String value) {
    this.name = name;
    this.value = value;
  }
}
