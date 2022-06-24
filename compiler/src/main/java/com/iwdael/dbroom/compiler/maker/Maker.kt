package com.iwdael.dbroom.compiler.maker

import javax.annotation.processing.Filer

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
interface Maker {
    companion object {
        const val ROOT_PACKAGE = "com.iwdael.dbroom"
    }
    fun classFull(): String
    fun className(): String
    fun packageName(): String
    fun make(filer: Filer)
}