package com.iwdael.dbroom.example

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotation.Delete
import com.iwdael.dbroom.annotation.Insert
import com.iwdael.dbroom.annotation.UseFlow

@UseFlow
@Entity(tableName = "TB_USER")
open class User(
    @PrimaryKey
    open var id: Int? = null,
    var name: String? = null,
    var address: String? = null,
    var isSelected: Boolean? = null,
) {

}