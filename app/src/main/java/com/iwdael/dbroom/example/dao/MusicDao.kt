package com.iwdael.dbroom.example.dao

import androidx.room.Dao
import androidx.room.Delete
import com.iwdael.dbroom.example.entity.Music

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
@Dao
interface MusicDao {
    @Delete(entity = Music::class)
    fun delete(vararg user: Music)
}