package com.iwdael.dbroom.compiler.maker

import androidx.annotation.Nullable
import androidx.databinding.Bindable
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asFileBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeName
import com.iwdael.annotationprocessorparser.poet.filePath
import com.iwdael.dbroom.annotations.UseGenerator
import com.iwdael.dbroom.compiler.KotlinClass.BASE_NOTIFIER
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import org.jetbrains.annotations.NotNull
import java.io.File

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class UseKotlinGenerator(val clazz: Class) {
    fun generate() {
        val useDataBinding = clazz.useDataBinding()
        clazz.asFileBuilder()
            .addType(
                clazz.asTypeBuilder()
                    .addSuperinterface(BASE_NOTIFIER)
                    .addModifiers(KModifier.OPEN)
                    .addAnnotations(
                        clazz.annotations
                            .filter { it.className != Metadata::class.java.name }
                            .filter { it.className != UseGenerator::class.java.name }
                            .map { it.asAnnotation() }
                    )
                    .addProperties(
                        clazz.roomFields().map {
                            PropertySpec
                                .builder(
                                    it.name,
                                    it.asTypeName().copy(true),
                                    KModifier.OPEN
                                )
                                .initializer("null")
                                .mutable(true)
                                .addAnnotations(it.annotation
                                    .filter { it.className != Bindable::class.java.name }
                                    .filter { it.className != NotNull::class.java.name }
                                    .filter { it.className != org.jetbrains.annotations.Nullable::class.java.name }
                                    .filter { it.className != Nullable::class.java.name }
                                    .map { it.asAnnotation() })
                                .apply {
                                    if (useDataBinding)
                                        addAnnotation(
                                            AnnotationSpec.builder(Bindable::class.java)
                                                .build()
                                        )
                                }
                                .build()
                        }
                    )
                    .build()
            )
            .addComment(FILE_COMMENT)
            .build()
            .toString()
            .replace("public ", "")
            .replace(": Unit {", " {")

            .let { File(clazz.filePath()!!).writeText(it) }
    }
}