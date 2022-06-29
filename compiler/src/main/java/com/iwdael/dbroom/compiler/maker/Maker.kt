package com.iwdael.dbroom.compiler.maker

import javax.annotation.processing.Filer

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
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