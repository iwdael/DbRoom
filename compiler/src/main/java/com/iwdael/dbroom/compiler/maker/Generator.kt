package com.iwdael.dbroom.compiler.maker

import javax.annotation.processing.Filer

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
interface Generator {
    val classNameGen: String
    val simpleClassNameGen: String
    val packageNameGen: String
    fun generate(filer: Filer)
}