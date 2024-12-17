package com.iwdael.dbroom.compiler.compat

import com.squareup.kotlinpoet.ClassName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */

var MASTER_PACKAGE = "com.iwdael.dbroom"
var ENABLED_COROUTINES = false
var DB_ROOM_SIMPLE_NAME = "DbRoom"
val ROOM_DATABASE = ClassName("androidx.room", "RoomDatabase")
val CONTEXT = ClassName("android.content", "Context")

val PERSISTENCE_ROOM get() = ClassName(MASTER_PACKAGE, "PersistenceRoom")
val CONVERTER get() = ClassName(MASTER_PACKAGE, "Converter")
val DB_ROOM: ClassName get() = ClassName(MASTER_PACKAGE, DB_ROOM_SIMPLE_NAME)
val PERSISTENCE = ClassName("com.iwdael.dbroom.core", "Persistence")