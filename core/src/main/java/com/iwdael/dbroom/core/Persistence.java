package com.iwdael.dbroom.core;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
@Entity(
        tableName = "persistence"
)
public class Persistence {
    @PrimaryKey
    @ColumnInfo(name = "persistence_name")
    @NotNull
    public String name;

    @ColumnInfo(name = "persistence_value")
    public String value;

    public Persistence(@NonNull String name, String value) {
        this.name = name;
        this.value = value;
    }
}
