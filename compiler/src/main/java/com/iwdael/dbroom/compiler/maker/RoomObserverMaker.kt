package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.annotation.UseDataBinding
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class RoomObserverMaker(private val generator: List<Generator>) : Maker {
    override fun classFull() = "${packageName()}.${className()}"
    override fun className() = "RoomObserver"
    override fun packageName() = "com.iwdael.dbroom"

    override fun make(filer: Filer) {
        val useDataBinding =
            generator.map { it.clazz }.any { it.getAnnotation(UseDataBinding::class.java) != null }
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(className())
                    .addField(
                        FieldSpec.builder(
                            ClassName.get(packageName(), "Observer"),
                            "dbObserver",
                            Modifier.PRIVATE, Modifier.FINAL
                        )
                            .addAnnotation(ClassName.bestGuess("androidx.room.Ignore"))
                            .build()
                    )
                    .superclass(ClassName.get(packageName(), "Observer"))
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
            if (generator.isEmpty()) return@apply
            generator.forEachIndexed { index, generator ->
                if (index == 0) {
                    beginControlFlow(
                        "if(clazz == \$T.class)",
                        ClassName.get(generator.packageName, generator.cn)
                    )
                    addStatement(
                        "dbObserver = new \$T((\$T) obj)",
                        ClassName.get(
                            generator.packageNameGenerator,
                            generator.cn + "Observer"
                        ), ClassName.get(
                            generator.packageName,
                            generator.cn
                        )
                    )
                } else {
                    nextControlFlow(
                        "else if(clazz == \$T.class)",
                        ClassName.get(generator.packageName, generator.cn)
                    )
                    addStatement(
                        "dbObserver = new \$T((\$T) obj)",
                        ClassName.get(
                            generator.packageNameGenerator,
                            generator.cn + "Observer"
                        ), ClassName.get(
                            generator.packageName,
                            generator.cn
                        )
                    )
                }
            }
            nextControlFlow("else")
            addStatement("dbObserver = null")
            endControlFlow()
        }
        .build()

}
