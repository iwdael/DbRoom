package com.iwdael.dbroom.compiler.generator

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
interface KotlinGenerator {
    val classNameGen: String
    val simpleClassNameGen: String
    val packageNameGen: String
    fun createFileSpec(): FileSpec
    fun generate(env: SymbolProcessorEnvironment) {
        try {
            createFileSpec().writeTo(env.codeGenerator, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}