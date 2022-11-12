package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.baseObservable
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.observerClassName
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class RoomObservableGenerator(private val classes: List<Class>) : Generator {
    override fun classFull() = "com.iwdael.dbroom.RoomObservable"
    override fun simpleClassName() = "RoomObservable"
    override fun packageName() = "com.iwdael.dbroom"

    override fun generate(filer: Filer) {
        val useDataBinding = classes.useDataBinding()
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addField(
                        FieldSpec.builder(baseObservable, "dbObservable")
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .addAnnotation(ClassName.bestGuess("androidx.room.Ignore"))
                            .build()
                    )
                    .superclass(baseObservable)
                    .apply {
                        if (useDataBinding) {
                            addMethod(
                                MethodSpec.methodBuilder("addOnPropertyChangedCallback")
                                    .addAnnotation(Override::class.java)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(
                                        ClassName.get(
                                            "androidx.databinding.Observable",
                                            "OnPropertyChangedCallback"
                                        ), "callback"
                                    )
                                    .addStatement("dbObservable.addOnPropertyChangedCallback(callback)")
                                    .build()
                            )


                            addMethod(
                                MethodSpec.methodBuilder("removeOnPropertyChangedCallback")
                                    .addAnnotation(Override::class.java)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(
                                        ClassName.get(
                                            "androidx.databinding.Observable",
                                            "OnPropertyChangedCallback"
                                        ), "callback"
                                    )
                                    .addStatement("dbObservable.removeOnPropertyChangedCallback(callback)")
                                    .build()
                            )
                        }
                    }
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addMethod(createConstructor())
                    .addMethod(
                        MethodSpec.methodBuilder("notifyPropertyChanged")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .addParameter(
                                ParameterSpec.builder(
                                    TypeName.INT, "fieldId"
                                ).build()
                            )
                            .addStatement("dbObservable.notifyPropertyChanged(fieldId)")
                            .build()

                    )
                    .addMethod(
                        MethodSpec.methodBuilder("getDbObservable")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(JavaClass.baseObservable)
                            .addStatement("return dbObservable")
                            .build()
                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }

    private fun createConstructor() = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("Class<?> clazz = this.getClass()")
        .addStatement("Object obj = this")
        .apply {
            if (classes.isEmpty()) return@apply
            classes.forEachIndexed { index, clazz ->
                if (index == 0) {
                    beginControlFlow("if(clazz == \$T.class)", clazz.asTypeName())
                    addStatement(
                        "dbObservable = new \$T((\$T) obj)",
                        clazz.observerClassName().asTypeName(), clazz.asTypeName()
                    )
                } else {
                    nextControlFlow("else if(clazz == \$T.class)", clazz.asTypeName())
                    addStatement(
                        "dbObservable = new \$T((\$T) obj)",
                        clazz.observerClassName().asTypeName(),
                        clazz.asTypeName()
                    )
                }
            }
            nextControlFlow("else")
            addStatement("dbObservable = null")
            endControlFlow()
        }
        .build()

}
