package com.iwdael.dblite.example

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "TB_POST")
data class Post(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo
    var name: String? = null,

    @ColumnInfo
    var address: String? = null
)