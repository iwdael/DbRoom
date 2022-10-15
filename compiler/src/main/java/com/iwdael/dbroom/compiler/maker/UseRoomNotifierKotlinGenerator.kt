package com.iwdael.dbroom.compiler.maker

import androidx.annotation.Nullable
import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asFileBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeName
import com.iwdael.annotationprocessorparser.poet.srcPath
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import com.iwdael.dbroom.compiler.KotlinClass.observable
import com.iwdael.dbroom.compiler.KotlinClass.roomObserver
import com.iwdael.dbroom.compiler.packageName
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.kotlinpoet.*
import org.jetbrains.annotations.NotNull
import java.io.File
import javax.lang.model.element.Modifier

class UseRoomNotifierKotlinGenerator(val clazz: Class) {
    fun handle() {
        val needGenerator = clazz.getAnnotation(UseRoomNotifier::class.java)!!.generate
        val useDataBinding = listOf(clazz).useDataBinding()
        if (!needGenerator) return
        clazz.asFileBuilder()
            .addType(
                clazz.asTypeBuilder()
                    .superclass(roomObserver)
                    .apply {
                        if (useDataBinding) addSuperinterface(observable)
                    }
                    .addAnnotation(
                        AnnotationSpec.builder(UseRoomNotifier::class.java)
                            .build()
                    )
                    .addAnnotations(
                        clazz.annotations
                            .filter { it.className != UseRoomNotifier::class.java.name }
                            .filter { it.className != Metadata::class.java.name }
                            .map { it.asAnnotation() }
                    )
                    .addProperties(
                        clazz.roomFields().map {
                            PropertySpec
                                .builder(
                                    it.name,
                                    it.asTypeName().copy(true),
                                    KModifier.PRIVATE
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
                    .addFunctions(
                        clazz.roomFields()
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map { it to it.setter }
                            .map { pair ->
                                val setter = pair.second
                                setter.asMethodBuilder()
                                    .addParameter(
                                        pair.first.name,
                                        pair.first.asTypeName().copy(true)
                                    )
                                    .addStatement("this.${pair.first.name} = ${pair.first.name}")
                                    .apply {
                                        if (pair.first.getAnnotation(PrimaryKey::class.java) == null)
                                            addStatement(
                                                "notifyPropertyChanged(%T.${pair.first.name})",
                                                ClassName.bestGuess(
                                                    "com.iwdael.dbroom.DB"
                                                )
                                            )
                                    }
                                    .build()
                            }
                    )
                    .addFunctions(
                        clazz.roomFields()
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map { it to it.getter }
                            .map { pair ->
                                val getter = pair.second
                                FunSpec.builder(getter.name)
                                    .returns(
                                        pair.first.asTypeName()
                                            .copy(true)
                                    )
                                    .addStatement("return this.${pair.first.name}")
                                    .build()
                            }
                    )
                    .build()
            )
            .build()
            .writeTo(File(clazz.srcPath(clazz.packageName())!!))

    }
}