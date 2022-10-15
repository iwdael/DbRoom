package com.iwdael.dbroom.compiler

import com.squareup.javapoet.ClassName

object JavaClass {
    val baseObserver = ClassName.bestGuess("com.iwdael.dbroom.Observer")
    val roomObserver = ClassName.get("com.iwdael.dbroom", "RoomObserver")
    val roomDatabase = ClassName.get("androidx.room", "RoomDatabase")

    val context = ClassName.get("android.content", "Context")

    val observable = ClassName.get("androidx.databinding", "Observable")

}