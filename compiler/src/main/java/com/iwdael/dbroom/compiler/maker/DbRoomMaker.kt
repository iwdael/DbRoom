package com.iwdael.dbroom.compiler.maker

import androidx.room.Database
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.firstLetterLowercase
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.maker.Maker.Companion.ROOT_PACKAGE
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


class DbRoomMaker(private val entities: List<Generator>, private val dao: List<Generator>) : Maker {

    override fun classFull() = "$ROOT_PACKAGE.${className()}"
    override fun className() = "DbRoom"
    override fun packageName() = ROOT_PACKAGE
    override fun make(filer: Filer) {
        val init = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .returns(Void.TYPE)
            .addParameter(ClassName.get("android.content", "Context"), "context")
            .addStatement("if (instance != null) return")
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("synchronized (DbRoom.class)")
                    .addStatement(CodeBlock.of("if (instance != null) return"))
                    .addStatement(
                        CodeBlock
                            .builder()
                            .add(
                                "instance = \$T.databaseBuilder(context.getApplicationContext(),DbRoom.class,\"lite.db\").build()",
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
            .addStatement("if (instance == null) throw new RuntimeException(\"Please initialize DbRoom first\")")
            .addStatement("return instance")
            .build()

        val store = MethodSpec.methodBuilder("store")
            .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
            .addParameter(String::class.java,"name")
            .addParameter(Object::class.java,"value")
            .addStatement("instance().holder().store(new Holder(name, HolderConverter.converterString(value)))")
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
                                    postfix = ",Holder.class}",
                                    prefix = "{"
                                )
                                add(
                                    fmt,
                                    *entities
                                        .map {
                                            ClassName.get(it.packageName, it.className)
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
            .addMethod(store)
            .apply {
                entities.forEach {
                    addMethod(
                        MethodSpec.methodBuilder(it.className.firstLetterLowercase())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(ClassName.get(it.packageNameGenerator, it.classNameGenerator))
                            .addStatement("return instance()._${it.className.firstLetterLowercase()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("_" + it.className.firstLetterLowercase())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                            .returns(ClassName.get(it.packageNameGenerator, it.classNameGenerator))
                            .build()
                    )
                }
                dao.forEach {
                    addMethod(
                        MethodSpec.methodBuilder(it.className.firstLetterLowercase())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(ClassName.get(it.packageName, it.className))
                            .addStatement("return instance()._${it.className.firstLetterLowercase()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("_" + it.className.firstLetterLowercase())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                            .returns(ClassName.get(it.packageName, it.className))
                            .build()
                    )
                }
            }
            .apply {
                addMethod(
                    MethodSpec.methodBuilder("holder")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                        .returns(ClassName.get(ROOT_PACKAGE, "HolderRoom"))
                        .build()
                )
            }
            .build()
        JavaFile
            .builder(packageName(), classTypeSpec)
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }


}