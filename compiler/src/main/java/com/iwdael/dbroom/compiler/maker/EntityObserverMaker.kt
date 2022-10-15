package com.iwdael.dbroom.compiler.maker

import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.*
import com.squareup.javapoet.*
import java.lang.ref.WeakReference
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class EntityObserverMaker(private val gen: Generator) : Maker {
    override fun classFull() = "${packageName()}.${className()}"
    override fun className() = "${gen.classSimpleName}Observer"
    override fun packageName() = gen.roomPackage

    override fun make(filer: Filer) {
        val useDataBinding = gen.clazz.getAnnotation(UseDataBinding::class.java) != null
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
                                gen.classSimpleName
                            ), gen.classSimpleName.charLower()
                        )
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build()
                    )
                    .apply {
                        if (useDataBinding) {
                            addField(
                                ClassName.get(
                                    "androidx.databinding",
                                    "PropertyChangeRegistry"
                                ), "callbacks",
                                Modifier.PRIVATE, Modifier.TRANSIENT
                            )

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
                                    .beginControlFlow("synchronized (this)")
                                    .beginControlFlow("if (callbacks == null)")
                                    .addStatement("callbacks = new PropertyChangeRegistry()")
                                    .endControlFlow()
                                    .endControlFlow()
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
                                    .beginControlFlow("synchronized (this)")
                                    .beginControlFlow("if (callbacks == null)")
                                    .addStatement("return")
                                    .endControlFlow()
                                    .endControlFlow()
                                    .addStatement("callbacks.remove(callback)")
                                    .build()
                            )
                        }
                    }
                    .apply {
                        gen.roomFields.filter { it.getAnnotation(PrimaryKey::class.java) == null }
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
                            gen.clazz.asTypeName()
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
                            .apply {
                                val getter = gen.roomPrimaryKeyField.getter
                                beginControlFlow(
                                    "if (null == ${gen.classSimpleName.charLower()}.${
                                        getter.name
                                    }())"
                                )
                            }
                            .addStatement("return")
                            .endControlFlow()
                            .apply {
                                if (gen.roomFields
                                        .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                        .isEmpty()
                                ) return@apply
                                gen.roomFields
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
                        gen.roomFields
                            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                            .map {
                                val key =
                                    gen.roomFields.first { it.getAnnotation(PrimaryKey::class.java) != null }
                                MethodSpec.methodBuilder("notify${it.name.charUpper()}Changed")
                                    .addModifiers(Modifier.PRIVATE)
                                    .apply {
                                        if (!useDataBinding) return@apply
                                        beginControlFlow("synchronized (this)")
                                            .beginControlFlow("if (callbacks != null)")
                                            .addStatement(
                                                "callbacks.notifyCallbacks(this, \$T.${it.name.charLower()}, null)",
                                                ClassName.get(
                                                    "androidx.databinding.library.baseAdapters",
                                                    "BR"
                                                )
                                            )
                                            .endControlFlow()
                                            .endControlFlow()
                                    }

                                    .addStatement("if (from(${gen.classSimpleName.charLower()}) == null) return")
                                    .addStatement("${it.name}EntityVersion++")
                                    .beginControlFlow("for (WeakReference<${gen.classSimpleName}> reference : all)")
                                    .addStatement("${gen.classSimpleName} entity = reference.get()")
                                    .addStatement("if (entity == null) continue")
                                    .addStatement("${gen.classSimpleName}Observer observer = from(entity)")
                                    .beginControlFlow("if (${it.name}EntityVersion > observer.${it.name}EntityVersion)")
                                    .addStatement("observer.${it.name}EntityVersion = ${it.name}EntityVersion - 1")
                                    .addStatement("observer.${it.name}RoomVersion = ${it.name}EntityVersion")
                                    .apply {
                                        val getter = gen.roomPrimaryKeyField.getter
                                            ?: throw IllegalArgumentException("Can not found getter(${gen.clazz.className}.${gen.roomPrimaryKeyField.name})")
                                        val setter = gen.roomPrimaryKeyField.setter
                                            ?: throw IllegalArgumentException("Can not found setter(${gen.clazz.className}.${gen.roomPrimaryKeyField.name})")
                                        addStatement(
                                            "entity.${setter.name}(${gen.classSimpleName.charLower()}.${getter.name}())"
                                        )
                                    }

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

                                            .apply {
                                                val getter = it.getter
                                                val keyGetter = key.getter
                                                addStatement(
                                                    "\$T.${gen.classSimpleName.charLower()}().update${it.name.charUpper()}(" +
                                                            "${gen.classSimpleName.charLower()}.${keyGetter.name}() ," +
                                                            "${gen.classSimpleName.charLower()}.${getter.name}()" +
                                                            ")",
                                                    dbRoomClassName()
                                                )
                                            }

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
                                    gen.classSimpleName
                                ),
                                gen.classSimpleName.charLower()
                            )
                            .addStatement("this.${gen.classSimpleName.charLower()} = ${gen.classSimpleName.charLower()}")
                            .addStatement("all.add(new WeakReference(${gen.classSimpleName.charLower()}))")
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
                                        gen.classSimpleName
                                    ), gen.classSimpleName
                                )
                                    .build()
                            )
                            .returns(
                                ClassName.get(
                                    gen.roomPackage,
                                    "${gen.classSimpleName}Observer"
                                )
                            )
                            .addStatement(
                                "\$T roomObserver = null",
                                ClassName.get("com.iwdael.dbroom", "RoomObserver")
                            )
                            .addStatement("Object target = ${gen.classSimpleName.charLower()}")
                            .beginControlFlow("if (target instanceof RoomObserver)")
                            .addStatement("roomObserver = (RoomObserver) target")
                            .endControlFlow()
                            .addStatement("if (roomObserver == null) return null")
                            .addStatement("return (${gen.classSimpleName}Observer) roomObserver.getDbObserver()")
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