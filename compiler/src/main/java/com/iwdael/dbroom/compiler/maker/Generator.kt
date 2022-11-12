package com.iwdael.dbroom.compiler.maker

import javax.annotation.processing.Filer

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
interface Generator {
    companion object {
        const val ROOT_PACKAGE = "com.iwdael.dbroom"
    }
    fun classFull(): String
    fun simpleClassName(): String
    fun packageName(): String
    fun generate(filer: Filer)
}