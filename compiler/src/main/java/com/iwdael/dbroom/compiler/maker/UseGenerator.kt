package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.annotations.UseGenerator

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class UseGenerator(private val classes: List<Class>) {
    fun generate() {
        classes
            .filter { it.getAnnotation(UseGenerator::class.java) != null }
            .forEach {
                if (it.getAnnotation(Metadata::class.java) == null)
                    UseJavaGenerator(it).generate()
                else
                    UseKotlinGenerator(it).generate()
            }
    }
}
