package com.iwdael.dbroom.core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
@Entity("persistence")
class Persistence {
    @PrimaryKey
    @ColumnInfo(name = "persistence_name")
    var name: String

    @ColumnInfo(name = "persistence_value")
    var value: String? = null

    constructor(name: String, value: String?) {
        this.name = name
        this.value = value
    }

    constructor() {
        this.name = ""
    }
}
