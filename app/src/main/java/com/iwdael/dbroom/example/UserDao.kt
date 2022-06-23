package com.iwdael.dbroom.example

import androidx.room.Dao
import androidx.room.Delete

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
@Dao
interface UserDao {
    @Delete(entity = User::class)
    fun delete(vararg user: User)
}