package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass.baseObserver
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.observerClassName
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class RoomObserverMaker(private val classes: List<Class>) : Generator {
    override fun classFull() = "com.iwdael.dbroom.RoomObserver"
    override fun simpleClassName() = "RoomObserver"
    override fun packageName() = "com.iwdael.dbroom"

    override fun generate(filer: Filer) {
        val useDataBinding = classes.useDataBinding()
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addField(
                        FieldSpec.builder(baseObserver, "dbObserver")
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .addAnnotation(ClassName.bestGuess("androidx.room.Ignore"))
                            .build()
                    )
                    .superclass(baseObserver)
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
                                    .addStatement("dbObserver.addOnPropertyChangedCallback(callback)")
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
                                    .addStatement("dbObserver.removeOnPropertyChangedCallback(callback)")
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
                            .addStatement("dbObserver.notifyPropertyChanged(fieldId)")
                            .build()

                    )
                    .addMethod(
                        MethodSpec.methodBuilder("getDbObserver")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ClassName.get("com.iwdael.dbroom", "Observer"))
                            .addStatement("return dbObserver")
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
                        "dbObserver = new \$T((\$T) obj)",
                        clazz.observerClassName().asTypeName(), clazz.asTypeName()
                    )
                } else {
                    nextControlFlow("else if(clazz == \$T.class)", clazz.asTypeName())
                    addStatement(
                        "dbObserver = new \$T((\$T) obj)",
                        clazz.observerClassName().asTypeName(),
                        clazz.asTypeName()
                    )
                }
            }
            nextControlFlow("else")
            addStatement("dbObserver = null")
            endControlFlow()
        }
        .build()

}
