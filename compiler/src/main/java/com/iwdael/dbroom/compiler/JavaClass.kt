package com.iwdael.dbroom.compiler

import com.squareup.javapoet.ClassName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
object JavaClass {
    val baseObservable = ClassName.bestGuess("com.iwdael.dbroom.core.BaseObservable")
    val roomObservable = ClassName.get("com.iwdael.dbroom.core", "RoomObservable")
    val roomDatabase = ClassName.get("androidx.room", "RoomDatabase")
    val context = ClassName.get("android.content", "Context")
    val observable = ClassName.get("androidx.databinding", "Observable")
    const val classNameOfObservable = "com.iwdael.dbroom.RoomObservableCreator"
}