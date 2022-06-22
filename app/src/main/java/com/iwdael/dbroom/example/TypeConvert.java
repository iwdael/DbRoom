package com.iwdael.dbroom.example;

import androidx.room.TypeConverter;

import com.iwdael.dbroom.example.entity.T;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public  class TypeConvert {
    @TypeConverter
    public static int  T2str(T t){
        return 0;
    }
    @TypeConverter
    public static T str2T(int t){
        return null;
    }
}
