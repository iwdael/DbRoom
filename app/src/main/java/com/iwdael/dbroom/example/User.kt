package com.iwdael.dbroom.example

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotation.UseFlow

@UseFlow
@Entity(tableName = "TB_USER")
open class User(
    @PrimaryKey
    var id: Int? = null,
    var name: String? = null,
    var address: String? = null,
    var isSelected: Boolean? = null,

    )