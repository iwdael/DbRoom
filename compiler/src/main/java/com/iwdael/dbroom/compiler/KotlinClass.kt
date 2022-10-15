package com.iwdael.dbroom.compiler

import com.squareup.kotlinpoet.ClassName

object KotlinClass {
    val roomObserver = ClassName.bestGuess("com.iwdael.dbroom.RoomObserver")
    val observable = ClassName.bestGuess("androidx.databinding.Observable")

}
