package com.iwdael.dbroom.example.util;

import androidx.room.TypeConverter;

import com.iwdael.dbroom.example.entity.Title;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public class TypeConvert {
    @TypeConverter
    public static int title2str(Title t) {
        return 0;
    }

    @TypeConverter
    public static Title str2T(int t) {
        return null;
    }
}
