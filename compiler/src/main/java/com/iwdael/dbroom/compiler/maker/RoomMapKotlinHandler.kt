package com.iwdael.dbroom.compiler.maker

import androidx.annotation.Nullable
import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeBuilder
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeName
import com.iwdael.annotationprocessorparser.poet.srcPath
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import com.iwdael.dbroom.compiler.Generator
import com.squareup.kotlinpoet.*
import org.jetbrains.annotations.NotNull
import java.io.File
import javax.lang.model.element.Modifier

class RoomMapKotlinHandler(val gen: Generator) {
    fun handle() {
        val dataBinding = gen.clazz.getAnnotation(UseDataBinding::class.java)
        val needGenerator = gen.clazz.getAnnotation(UseRoomNotifier::class.java)!!.generate
        val useDataBinding = dataBinding != null
        if (!needGenerator) return
        FileSpec.builder(gen.clazz.packet.name, gen.clazz.classSimpleName)
            .addType(
                gen.clazz.asTypeBuilder()
                    .superclass(ClassName.bestGuess("com.iwdael.dbroom.RoomObserver"))
                    .apply {
                        if (useDataBinding) addSuperinterface(
                            ClassName.bestGuess("androidx.databinding.Observable")
                        )
                    }
                    .addAnnotation(
                        AnnotationSpec.builder(UseRoomNotifier::class.java)
                            .addMember(CodeBlock.of("generate = false"))
                            .build()
                    )
                    .addAnnotations(
                        gen.clazz.annotations
                            .filter { it.className != UseRoomNotifier::class.java.name }
                            .filter { it.className != Metadata::class.java.name }
                            .map { annotation -> annotation.asAnnotation() }
                    )
                    .addProperties(
                        gen.roomFields.map {
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
                                    .map { annotation -> annotation.asAnnotation() })
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
                        gen.roomFields
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map {
                                it to (it.setter
                                    ?: throw IllegalArgumentException("Can not found setter(${it.parent.className}.${it.name})"))
                            }
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
                        gen.roomFields
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map {
                                it to (it.getter
                                    ?: throw IllegalArgumentException("Can not found getter(${it.parent.className}.${it.name})"))
                            }
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
            .writeTo(File(gen.clazz.srcPath(gen.clazz.packet.name)!!))

    }
}