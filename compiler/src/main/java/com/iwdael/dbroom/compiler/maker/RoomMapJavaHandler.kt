package com.iwdael.dbroom.compiler.maker

import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asFieldBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickModifier
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickParameter
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickReturn
import com.iwdael.annotationprocessorparser.poet.srcPath
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import com.iwdael.dbroom.compiler.Generator
import com.squareup.javapoet.*
import java.io.File
import javax.lang.model.element.Modifier

class RoomMapJavaHandler(val gen: Generator) {
    fun handle() {
        val dataBinding = gen.clazz.getAnnotation(UseDataBinding::class.java)
        val needGenerator = gen.clazz.getAnnotation(UseRoomNotifier::class.java)!!.generate
        val useDataBinding = dataBinding != null
        if (!needGenerator) return
        JavaFile
            .builder(
                gen.clazz.packet.name, TypeSpec.classBuilder(gen.clazz.classSimpleName)
                    .superclass(ClassName.get("com.iwdael.dbroom", "RoomObserver"))
                    .apply {
                        if (useDataBinding) addSuperinterface(
                            ClassName.get(
                                "androidx.databinding",
                                "Observable"
                            )
                        )
                    }
                    .addModifiers(*gen.clazz.modifiers.toTypedArray())
                    .addAnnotation(
                        AnnotationSpec.builder(UseRoomNotifier::class.java)
                            .addMember("generate", "false")
                            .build()
                    )
                    .addAnnotations(gen.clazz.annotations
                        .filter { it.className != UseRoomNotifier::class.java.name }
                        .map { annotation ->
                            annotation.asAnnotation()
                        })
                    .addFields(
                        gen.roomFields.map {
                            it.asFieldBuilder()
                                .addAnnotations(it.annotation
                                    .filter { it.className != Bindable::class.java.name }
                                    .map { annotation -> annotation.asAnnotation() })
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
                        gen.roomFields
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map {
                                it to (it.setter
                                    ?: throw IllegalArgumentException("Can not found setter(${it.parent.className}.${it.name})"))
                            }
                            .map { pair ->
                                val setter = pair.second
                                setter.asMethodBuilder()
                                    .stickModifier()
                                    .stickParameter()
                                    .stickAnnotation()
                                    .addStatement("this.${pair.first.name} = ${pair.first.name}")
                                    .apply {
                                        if (pair.first.getAnnotation(PrimaryKey::class.java) == null)
                                            addStatement(
                                                "notifyPropertyChanged(\$T.${pair.first.name})",
                                                ClassName.get("com.iwdael.dbroom", "DB")
                                            )
                                    }
                                    .build()
                            }
                    )
                    .addMethods(
                        gen.roomFields
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map {
                                it to (it.getter
                                    ?: throw IllegalArgumentException("Can not found setter(${it.parent.className}.${it.name})"))
                            }
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
            .build()
            .writeTo(File(gen.clazz.srcPath(gen.clazz.packet.name)!!))
    }
}