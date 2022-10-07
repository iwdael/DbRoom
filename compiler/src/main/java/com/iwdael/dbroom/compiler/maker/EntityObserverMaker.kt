package com.iwdael.dbroom.compiler.maker

import androidx.room.PrimaryKey
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.*
import com.squareup.javapoet.*
import java.lang.ref.WeakReference
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class EntityObserverMaker(private val gen: Generator) : Maker {
    override fun classFull() = "${packageName()}.${className()}"
    override fun className() = "${gen.cn}Observer"
    override fun packageName() = gen.packageNameGenerator

    override fun make(filer: Filer) {
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .superclass(ClassName.get("com.iwdael.dbroom", "Observer"))
                    .addModifiers(Modifier.PUBLIC)
                    .addField(
                        FieldSpec.builder(
                            ClassName.get(
                                gen.packageName,
                                gen.cn
                            ), gen.cn.charLower()
                        )
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build()
                    )
                    .apply {
                        gen.fields.filter { it.getAnnotation(PrimaryKey::class.java) == null }
                            .forEach {
                                addField(
                                    FieldSpec.builder(TypeName.INT, "${it.name}RoomVersion")
                                        .addModifiers(Modifier.PRIVATE)
                                        .initializer("0")
                                        .build()
                                )
                                addField(
                                    FieldSpec.builder(TypeName.INT, "${it.name}EntityVersion")
                                        .addModifiers(Modifier.PRIVATE)
                                        .initializer("0")
                                        .build()
                                )
                            }
                    }
                    .apply {
                        val weak = ParameterizedTypeName.get(
                            ClassName.get(WeakReference::class.java),
                            ClassName.get(gen.clazz.`package`.name, gen.clazz.name)
                        )
                        val list = ParameterizedTypeName.get(ClassName.get(List::class.java), weak)
                        addField(
                            FieldSpec.builder(list, "all")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .initializer("new \$T<>()", ClassName.get(ArrayList::class.java))
                                .build()
                        )
                    }
                    .addMethod(
                        MethodSpec
                            .methodBuilder("notifyPropertyChanged")
                            .addAnnotation(Override::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                ParameterSpec.builder(
                                    TypeName.INT, "fieldId"
                                ).build()
                            )
                            .addStatement("cleanCache()")
                            .beginControlFlow("if (null == ${gen.cn.charLower()}.${
                                gen.getter(
                                    gen.fields.first { it.getAnnotation(PrimaryKey::class.java) != null }
                                )
                            }())")
                            .addStatement("return")
                            .endControlFlow()
                            .apply {
                                if (gen.fields
                                        .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                        .isEmpty()
                                ) return@apply
                                gen.fields
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .forEachIndexed { index, field ->
                                        if (index == 0) {
                                            beginControlFlow(
                                                "if (fieldId == \$T.${field.name})", dbClassName()
                                            )
                                        } else {
                                            nextControlFlow(
                                                "else if (fieldId == \$T.${field.name})",
                                                dbClassName()
                                            )
                                        }
                                        addStatement("notify${field.name.charUpper()}Changed()")
                                    }
                                endControlFlow()
                            }
                            .build()
                    )
                    .addMethods(
                        gen.fields
                            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                            .map {
                                val key =
                                    gen.fields.first { it.getAnnotation(PrimaryKey::class.java) != null }
                                MethodSpec.methodBuilder("notify${it.name.charUpper()}Changed")
                                    .addModifiers(Modifier.PRIVATE)
                                    .addStatement("if (from(${gen.cn.charLower()}) == null) return")
                                    .addStatement("${it.name}EntityVersion++")
                                    .beginControlFlow("for (WeakReference<${gen.cn}> reference : all)")
                                    .addStatement("if (reference.get() == null) continue")
                                    .addStatement("${gen.cn} entity = reference.get()")
                                    .addStatement("if (entity == null) continue")
                                    .addStatement("${gen.cn}Observer observer = from(entity)")
                                    .beginControlFlow("if (${it.name}EntityVersion > observer.${it.name}EntityVersion)")
                                    .addStatement("observer.${it.name}EntityVersion = ${it.name}EntityVersion - 1")
                                    .addStatement("observer.${it.name}RoomVersion = ${it.name}EntityVersion")
                                    .addStatement(
                                        "entity.${gen.setter(it)}(${gen.cn.charLower()}.${
                                            gen.getter(it)
                                        }())"
                                    )
                                    .endControlFlow()
                                    .endControlFlow()
                                    .addStatement("if (${it.name}RoomVersion >= ${it.name}EntityVersion) return")
                                    .addStatement("${it.name}RoomVersion = ${it.name}EntityVersion - 1")
                                    .addCode(
                                        CodeBlock.builder()
                                            .beginControlFlow("notifyRoom(new RoomNotifier()")
                                            .add("@Override\n")
                                            .beginControlFlow("public void notifier()")
                                            .beginControlFlow("if (${it.name}EntityVersion - ${it.name}RoomVersion == 1)")
                                            .addStatement("${it.name}RoomVersion = ${it.name}EntityVersion")
                                            .addStatement(
                                                "\$T.${gen.cn.charLower()}().update${it.name.charUpper()}(" +
                                                        "${gen.cn.charLower()}.${gen.getter(key)}() ," +
                                                        "${gen.cn.charLower()}.${gen.getter(it)}()" +
                                                        ")",
                                                dbRoomClassName()
                                            )
                                            .endControlFlow()
                                            .endControlFlow()
                                            .unindent()
                                            .addStatement("})")
                                            .build()
                                    )

                                    .build()
                            }
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                ClassName.get(
                                    gen.packageName,
                                    gen.cn
                                ),
                                gen.cn.charLower()
                            )
                            .addStatement("this.${gen.cn.charLower()} = ${gen.cn.charLower()}")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("cleanCache")
                            .addModifiers(Modifier.PRIVATE)
                            .addStatement("int size = all.size()")
                            .beginControlFlow("for (int index = size; index > 0; index--)")
                            .beginControlFlow("if (null == all.get(index).get())")
                            .addStatement("all.remove(index)")
                            .endControlFlow()
                            .endControlFlow()
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("from")
                            .addModifiers(Modifier.PRIVATE)
                            .addParameter(
                                ParameterSpec.builder(
                                    ClassName.get(
                                        gen.packageName,
                                        gen.cn
                                    ), gen.cn
                                )
                                    .build()
                            )
                            .returns(
                                ClassName.get(
                                    gen.packageNameGenerator,
                                    "${gen.cn}Observer"
                                )
                            )
                            .addStatement(
                                "\$T roomObserver = null",
                                ClassName.get("com.iwdael.dbroom", "RoomObserver")
                            )
                            .addStatement("Object target = ${gen.cn.charLower()}")
                            .beginControlFlow("if (target instanceof RoomObserver)")
                            .addStatement("roomObserver = (RoomObserver) target")
                            .endControlFlow()
                            .addStatement("if (roomObserver == null) return null")
                            .addStatement("return (${gen.cn}Observer) roomObserver.getDbObserver()")
                            .build()
                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }

    fun dbClassName() = ClassName.get("com.iwdael.dbroom", "DB")
    fun dbRoomClassName() = ClassName.get("com.iwdael.dbroom", "DbRoom")
}