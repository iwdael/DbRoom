package com.iwdael.dblite.compiler.maker

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
interface Maker {
    fun classFull(): String
    fun className(): String
    fun packageName(): String
    fun make(): String
}