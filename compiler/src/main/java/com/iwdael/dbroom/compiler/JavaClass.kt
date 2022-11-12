package com.iwdael.dbroom.compiler

import com.squareup.javapoet.ClassName

object JavaClass {
    val baseObservable = ClassName.bestGuess("com.iwdael.dbroom.BaseObservable")
    val roomObservable = ClassName.get("com.iwdael.dbroom", "RoomObservable")
    val roomDatabase = ClassName.get("androidx.room", "RoomDatabase")

    val context = ClassName.get("android.content", "Context")

    val observable = ClassName.get("androidx.databinding", "Observable")

}