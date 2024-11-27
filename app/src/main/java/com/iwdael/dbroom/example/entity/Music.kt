package com.iwdael.dbroom.example.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Music {
    @PrimaryKey
    var id: Long? = null
    var name: String? = null
    var artists: String? = null
    var duration: String? = null
    var lyrics: String? = null
}
