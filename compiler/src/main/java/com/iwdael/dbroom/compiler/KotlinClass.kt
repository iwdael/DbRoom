package com.iwdael.dbroom.compiler

import com.squareup.kotlinpoet.ClassName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
object KotlinClass {
    val roomObserver = ClassName.bestGuess("com.iwdael.dbroom.core.RoomObservable")
    val observable = ClassName.bestGuess("androidx.databinding.Observable")
}
