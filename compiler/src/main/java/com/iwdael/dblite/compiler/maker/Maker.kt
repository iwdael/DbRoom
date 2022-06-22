package com.iwdael.dblite.compiler.maker

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer

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
    fun make(filer: Filer)
}