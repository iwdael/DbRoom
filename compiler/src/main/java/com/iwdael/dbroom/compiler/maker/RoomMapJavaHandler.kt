package com.iwdael.dbroom.compiler.maker

import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Annotation
import com.iwdael.dbroom.annotation.UseDataBinding
import com.iwdael.dbroom.annotation.UseRoomNotifier
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.*
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
                gen.clazz.`package`.name, TypeSpec.classBuilder(gen.clazz.name)
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
                        .filter { it.name != UseRoomNotifier::class.java.name }
                        .map { annotation ->
                            AnnotationSpec
                                .builder(ClassName.bestGuess(annotation.name))
                                .apply {
                                    annotation.values
                                        .forEach { pair ->
                                            addMember(
                                                pair.first.toString(),
                                                pair.second.toAnnotationValue()
                                            )
                                        }
                                }
                                .build()
                        })
                    .addFields(
                        gen.fields.map {
                            FieldSpec
                                .builder(
                                    it.type.bestGuessClassName(),
                                    it.name,
                                    *it.modifiers.toTypedArray()
                                )
                                .addAnnotations(it.annotation
                                    .filter { it.name != Bindable::class.java.name }
                                    .map { annotation ->
                                        AnnotationSpec
                                            .builder(ClassName.bestGuess(annotation.name))
                                            .apply {
                                                annotation.values.forEach { pair ->
                                                    addMember(
                                                        pair.first.toString(),
                                                        pair.second.toAnnotationValue()
                                                    )
                                                }
                                            }
                                            .build()
                                    })
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
                        gen.fields
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map { it to gen.setterMethod(it) }
                            .map { pair ->
                                val setter = pair.second
                                MethodSpec.methodBuilder(setter.name)
                                    .addModifiers(
                                        *setter.modifiers.toTypedArray()
                                    )
                                    .addParameter(
                                        setter.parameter[0].type.bestGuessClassName(),
                                        pair.first.name
                                    )
                                    .addAnnotations(
                                        setter.e.annotationMirrors.map { Annotation(it) }
                                            .map { annotation ->
                                                AnnotationSpec
                                                    .builder(ClassName.bestGuess(annotation.name))
                                                    .apply {
                                                        annotation.values.forEach { pair ->
                                                            addMember(
                                                                pair.first.toString(),
                                                                pair.second.toAnnotationValue()
                                                            )
                                                        }
                                                    }
                                                    .build()
                                            }
                                    )
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
                        gen.fields
                            .filter { !it.modifiers.contains(Modifier.STATIC) }
                            .map { it to gen.getterMethod(it) }
                            .map { pair ->
                                val getter = pair.second
                                MethodSpec.methodBuilder(getter.name)
                                    .addModifiers(*getter.modifiers.toTypedArray())
                                    .addAnnotations(
                                        getter.e.annotationMirrors.map { Annotation(it) }
                                            .map { annotation ->
                                                AnnotationSpec
                                                    .builder(ClassName.bestGuess(annotation.name))
                                                    .apply {
                                                        annotation.values.forEach { pair ->
                                                            addMember(
                                                                pair.first.toString(),
                                                                pair.second.toAnnotationValue()
                                                            )
                                                        }
                                                    }
                                                    .build()
                                            }
                                    )
                                    .returns(pair.first.type.bestGuessClassName())
                                    .addStatement("return this.${pair.first.name}")
                                    .build()
                            }
                    )
                    .build()
            )
            .build()
            .writeTo(File(gen.clazz.srcPath(gen.clazz.`package`.name)!!))
    }
}