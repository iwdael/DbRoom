package com.iwdael.dbroom.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey
    var id: Int? = null,

    var name: String? = null,

    var address: String? = null,
)