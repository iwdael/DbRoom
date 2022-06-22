package com.iwdael.dblite.compiler.maker

import androidx.room.Database
import com.iwdael.dblite.compiler.DTA
import com.iwdael.dblite.compiler.compat.firstLetterLowercase
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


class DbLiteMaker(private val entities: List<DTA>) : Maker {
    companion object {
        const val ROOT_PACKAGE = "com.iwdael.dblite"
    }

    override fun classFull() = "com.iwdael.dblite.DbLite"
    override fun className() = "DbLite"
    override fun packageName() = ROOT_PACKAGE
    override fun make(filer: Filer) {
        val init = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .returns(Void.TYPE)
            .addParameter(ClassName.get("android.content", "Context"), "context")
            .addStatement("if (instance != null) return")
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("synchronized (DbLite.class)")
                    .addStatement(CodeBlock.of("if (instance != null) return"))
                    .addStatement(
                        CodeBlock
                            .builder()
                            .add(
                                "instance = \$T.databaseBuilder(context.getApplicationContext(),DbLite.class,\"lite.db\").build()",
                                ClassName.get("androidx.room", "Room")
                            )
                            .build()
                    )
                    .endControlFlow()
                    .build()
            )
            .build()


        val instance = MethodSpec.methodBuilder("instance")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .returns(ClassName.get(packageName(), className()))
            .addStatement("if (instance == null) throw new RuntimeException(\"Please initialize DbLite first\")")
            .addStatement("return instance")
            .build()

        val classTypeSpec = TypeSpec.classBuilder(className())
            .addModifiers(Modifier.ABSTRACT)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(
                AnnotationSpec.builder(Database::class.java)
                    .addMember(
                        "entities",
                        CodeBlock.builder()
                            .apply {
                                val fmt = entities.joinToString(
                                    separator = ",",
                                    transform = { "\$T.class" },
                                    postfix = "}",
                                    prefix = "{"
                                )
                                add(
                                    fmt,
                                    *entities
                                        .map {
                                            ClassName.get(it.packageName, it.targetClassName)
                                        }
                                        .toTypedArray()
                                )
                            }
                            .build()
                    )
                    .addMember("version", CodeBlock.builder().add("1").build())
                    .addMember("exportSchema", CodeBlock.of("false"))
                    .build()
            )
            .addField(
                ClassName.get(packageName(), className()),
                "instance",
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.VOLATILE
            )
            .superclass(ClassName.get("androidx.room", "RoomDatabase"))
            .addMethod(init)
            .addMethod(instance)
            .apply {
                entities.forEach {
                    addMethod(
                        MethodSpec.methodBuilder("r${it.targetClassName}")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(ClassName.get(ROOT_PACKAGE, it.generatedClassName))
                            .addStatement("return instance().${it.targetClassName.firstLetterLowercase()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder(it.targetClassName.firstLetterLowercase())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .returns(ClassName.get(ROOT_PACKAGE, it.generatedClassName))
                            .build()
                    )
                }
            }
            .build()
        JavaFile
            .builder(packageName(), classTypeSpec)
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .writeTo(filer)
    }


}