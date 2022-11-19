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
import com.iwdael.dbroom.compiler.JavaClass.ROOM_OBSERVABLE
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.packageName
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.useDataBinding
import com.iwdael.dbroom.compiler.useRoomNotifier
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
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
        val useRoomNotifier = clazz.useRoomNotifier()
        if (!useRoomNotifier && !useDataBinding) return
        JavaFile
            .builder(
                clazz.packageName(), TypeSpec.classBuilder(clazz.classSimpleName)
                    .superclass(ROOM_OBSERVABLE)
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
                                    .addStatement(
                                        "notifyPropertyChanged(\$T.${pair.first.name})",
                                        ClassName.get("com.iwdael.dbroom", "DB")
                                    )
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
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .writeTo(File(clazz.srcPath(clazz.packageName())!!))
    }
}