package com.iwdael.dbroom.compiler.maker

import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asFieldBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickModifier
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickParameter
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickReturn
import com.iwdael.annotationprocessorparser.poet.srcPath
import com.iwdael.dbroom.annotations.UseRoomNotifier
import com.iwdael.dbroom.compiler.JavaClass.observable
import com.iwdael.dbroom.compiler.JavaClass.roomObservable
import com.iwdael.dbroom.compiler.packageName
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.javapoet.*
import java.io.File
import javax.lang.model.element.Modifier

class UseRoomNotifierJavaGenerator(val clazz: Class) {
    fun generate() {
        val needGenerator = clazz.getAnnotation(UseRoomNotifier::class.java)!!.generate
        val useDataBinding = listOf(clazz).useDataBinding()
        if (!needGenerator) return
        JavaFile
            .builder(
                clazz.packageName(), TypeSpec.classBuilder(clazz.classSimpleName)
                    .superclass(roomObservable)
                    .apply {
                        if (useDataBinding) addSuperinterface(observable)
                    }
                    .addModifiers(*clazz.modifiers.toTypedArray())
                    .addAnnotation(
                        AnnotationSpec.builder(UseRoomNotifier::class.java)
                            .build()
                    )
                    .addAnnotations(clazz.annotations
                        .filter { it.className != UseRoomNotifier::class.java.name }
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
            .build()
            .writeTo(File(clazz.srcPath(clazz.packageName())!!))
    }
}