package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.useDataBinding
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class ObserverGenerator(private val classes: List<Class>) : Generator {
    override fun classFull() = "com.iwdael.dbroom.Observer"
    override fun simpleClassName() = "Observer"
    override fun packageName() = "com.iwdael.dbroom"

    override fun generate(filer: Filer) {
        val useDataBinding = classes.useDataBinding()
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addType(
                        TypeSpec.interfaceBuilder("RoomNotifier")
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(
                                MethodSpec.methodBuilder("notifier")
                                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                    .build()
                            )
                            .build()
                    )
                    .apply {
                        if (useDataBinding) {
                            addSuperinterface(ClassName.bestGuess("androidx.databinding.Observable"))
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
                                    .build()
                            )
                        }
                    }
                    .addField(
                        FieldSpec.builder(ClassName.bestGuess("android.os.Handler"), "handler")
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE)
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("checkAndInit")
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                            .beginControlFlow("if (handler == null)")
                            .beginControlFlow("synchronized (Observer.class)")
                            .beginControlFlow("if (handler == null)")
                            .addStatement(
                                "\$T thread = new HandlerThread(\"room-update\")",
                                ClassName.bestGuess("android.os.HandlerThread")
                            )
                            .addStatement("thread.start()")
                            .addStatement("handler = new Handler(thread.getLooper())")
                            .endControlFlow()
                            .endControlFlow()
                            .endControlFlow()
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("notifyRoom")
                            .addModifiers(Modifier.PROTECTED)
                            .addParameter(
                                ClassName.get(
                                    "com.iwdael.dbroom.Observer",
                                    "RoomNotifier"
                                ),
                                "notifier"
                            )
                            .addStatement("checkAndInit()")
                            .addCode(
                                CodeBlock.builder()
                                    .beginControlFlow("handler.post(new Runnable()")
                                    .add("@Override\n")
                                    .beginControlFlow("public void run()")
                                    .addStatement("notifier.notifier()")
                                    .endControlFlow()
                                    .unindent()
                                    .addStatement("})")
                                    .build()
                            )

                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("notifyPropertyChanged")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(
                                ParameterSpec.builder(
                                    TypeName.INT, "fieldId"
                                ).build()
                            )
                            .build()

                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }

}