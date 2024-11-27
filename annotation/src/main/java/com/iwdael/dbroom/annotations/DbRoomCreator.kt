package com.iwdael.dbroom.annotations

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.BINARY)
annotation class DbRoomCreator(val value: String = "com.iwdael.dbroom.DbRoom", val version: Int = 1, val exportSchema: Boolean = true)