package com.iwdael.dbroom.example

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotation.Delete
import com.iwdael.dbroom.annotation.Find
import com.iwdael.dbroom.annotation.Update

@Entity
data class Post(
    @PrimaryKey
    var id: Int? = null,

    @Update(value = ["updateInfo"], where = ["updateInfo"])
    var name: String? = null,

    @Update(value = ["updateInfo"])
    @Delete("deleteAddress")
    @Find("findAddress")
    var address: String? = null,
)