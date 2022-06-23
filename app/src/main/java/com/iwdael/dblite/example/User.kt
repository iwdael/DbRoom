package com.iwdael.dblite.example

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "TB_USER")
data class User(
    @PrimaryKey
    var id: Int? = null,

    var name: String? = null,

    var address: String? = null,
)