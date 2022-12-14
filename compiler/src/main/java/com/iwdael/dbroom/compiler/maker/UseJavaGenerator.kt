package com.iwdael.dbroom.compiler.maker

import androidx.databinding.Bindable
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asFieldBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickModifier
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickParameter
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickReturn
import com.iwdael.annotationprocessorparser.poet.srcPath
import com.iwdael.dbroom.annotations.UseGenerator
import com.iwdael.dbroom.compiler.JavaClass.BASE_NOTIFIER
import com.iwdael.dbroom.compiler.JavaClass.INDENT
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.packageName
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.File
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class UseJavaGenerator(val clazz: Class) {
    fun generate() {
        val useDataBinding = clazz.useDataBinding()
        JavaFile
            .builder(
                clazz.packageName(), TypeSpec.classBuilder(clazz.classSimpleName)
                    .addSuperinterface(BASE_NOTIFIER)
                    .addModifiers(*clazz.modifiers.toTypedArray())
                    .addAnnotations(clazz.annotations
                        .filter { it.className != UseGenerator::class.java.name }
                        .map { annotation ->
                            annotation.asAnnotation()
                        })
                    .addFields(
                        clazz.roomFields().map {
                            it.asFieldBuilder()
                                .stickModifier()
                                .addAnnotations(it.annotation
                                    .filter { it.className != Bindable::class.java.name }
                                    .map { it.asAnnotation() })
                                .apply {
                                    if (useDataBinding)
                                        addAnnotation(
                                            AnnotationSpec.builder(Bindable::class.java).build()
                                        )
                                }
                                .build()
                        }
                    )
                    .addMethods(
                        clazz.roomFields()
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map { it to it.setter }
                            .map { pair ->
                                val setter = pair.second
                                setter.asMethodBuilder()
                                    .stickModifier()
                                    .stickParameter()
                                    .stickAnnotation()
                                    .addStatement("this.${pair.first.name} = ${pair.first.name}")
                                    .build()
                            }
                    )
                    .addMethods(
                        clazz.roomFields()
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map { it to it.getter }
                            .map { pair ->
                                val getter = pair.second
                                getter
                                    .asMethodBuilder()
                                    .stickModifier()
                                    .stickAnnotation()
                                    .stickReturn()
                                    .addStatement("return this.${pair.first.name}")
                                    .build()
                            }
                    )
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(INDENT)
            .build()
            .writeTo(File(clazz.srcPath(clazz.packageName())!!))
    }
}