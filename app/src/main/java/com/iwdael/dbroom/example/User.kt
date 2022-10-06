package com.iwdael.dbroom.example

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotation.Delete
import com.iwdael.dbroom.annotation.Insert
import com.iwdael.dbroom.annotation.UseFlow

@UseFlow
@Entity(tableName = "TB_USER")
data class User(
    @PrimaryKey
    var id: Int? = null,
    var name: String? = null,
    var address: String? = null,
) {
    override fun toString(): String {
        return "User(" +
                "id=$id, " +
                "name=$name, " +
                "address=$address" +
                ")"
    }
}