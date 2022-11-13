package com.iwdael.dbroom.compiler.maker

import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.charLower
import com.iwdael.dbroom.compiler.compat.charUpper
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import java.lang.ref.WeakReference
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityObservableGenerator(private val clazz: Class) : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName() = "${clazz.classSimpleName}Observable"
    override fun packageName() = clazz.roomPackage()

    override fun generate(filer: Filer) {
        val useDataBinding = clazz.useDataBinding()
        val useRoomNotifier = clazz.useRoomNotifier()
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(simpleClassName())
                    .superclass(JavaClass.baseObservable)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(
                        FieldSpec.builder(clazz.asTypeName(), clazz.classSimpleName.charLower())
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
                                    .addStatement("callbacks.add(callback)")
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
                        clazz.roomFields()
                            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                            .forEach {
                                addField(
                                    FieldSpec.builder(TypeName.INT, "${it.name}RoomVersion")
                                        .addModifiers(Modifier.PUBLIC)
                                        .initializer("-1")
                                        .build()
                                )
                                addField(
                                    FieldSpec.builder(TypeName.INT, "${it.name}EntityVersion")
                                        .addModifiers(Modifier.PUBLIC)
                                        .initializer("-1")
                                        .build()
                                )
                            }
                    }
                    .apply {
                        val weak = ParameterizedTypeName.get(
                            ClassName.get(WeakReference::class.java),
                            clazz.asTypeName()
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
                                val getter = clazz.roomPrimaryKeyField().getter
                                beginControlFlow(
                                    "if (null == ${clazz.classSimpleName.charLower()}.${
                                        getter.name
                                    }())"
                                )
                            }
                            .addStatement("return")
                            .endControlFlow()
                            .apply {
                                if (clazz.roomFields()
                                        .none { it.getAnnotation(PrimaryKey::class.java) == null }
                                ) return@apply
                                clazz.roomFields()
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
                        clazz.roomFields()
                            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                            .map {
                                MethodSpec.methodBuilder("notify${it.name.charUpper()}Changed")
                                    .addModifiers(Modifier.PRIVATE)
                                    .apply {
                                        if (!useDataBinding) return@apply
                                        beginControlFlow("synchronized (this)")
                                            .beginControlFlow("if (callbacks != null)")
                                            .addStatement(
                                                "callbacks.notifyCallbacks(${clazz.classSimpleName.charLower()}, \$T.${it.name.charLower()}, null)",
                                                ClassName.get(
                                                    "androidx.databinding.library.baseAdapters",
                                                    "BR"
                                                )
                                            )
                                            .endControlFlow()
                                            .endControlFlow()
                                    }
                                    .apply {
                                        if (!useRoomNotifier) return@apply
                                        this.addStatement("if (from(${clazz.classSimpleName.charLower()}) == null) return")
                                            .beginControlFlow("if (${it.name}EntityVersion == -1)")
                                            .addStatement("int maxVersion = 0")
                                            .addStatement("List<WeakReference<${clazz.classSimpleName}>> ${clazz.classSimpleName.charLower()}OfInit = ${clazz.classSimpleName.charLower()}OfAll()")
                                            .beginControlFlow("for (WeakReference<${clazz.classSimpleName}> reference : ${clazz.classSimpleName.charLower()}OfInit)")
                                            .addStatement("${clazz.classSimpleName} entity = reference.get()")
                                            .addStatement("if (entity == null) continue")
                                            .addStatement(
                                                "\$T observer = from(entity)",
                                                clazz.observerClassName().asTypeName()
                                            )
                                            .addStatement("maxVersion = Math.max(observer.${it.name}EntityVersion, maxVersion)")
                                            .endControlFlow()
                                            .addStatement("${it.name}EntityVersion = maxVersion")
                                            .addStatement("return")
                                            .endControlFlow()
                                            .addStatement("${it.name}EntityVersion++")
                                            .addStatement("List<WeakReference<${clazz.classSimpleName}>> ${clazz.classSimpleName.charLower()}OfAll = ${clazz.classSimpleName.charLower()}OfAll()")
                                            .beginControlFlow("for (WeakReference<${clazz.classSimpleName}> reference : ${clazz.classSimpleName.charLower()}OfAll)")
                                            .addStatement("${clazz.classSimpleName} entity = reference.get()")
                                            .addStatement("if (entity == null) continue")
                                            .addStatement(
                                                "\$T observer = from(entity)",
                                                clazz.observerClassName().asTypeName()
                                            )
                                            .beginControlFlow("if (${it.name}EntityVersion > observer.${it.name}EntityVersion)")
                                            .addStatement("observer.${it.name}EntityVersion = ${it.name}EntityVersion - 1")
                                            .addStatement("observer.${it.name}RoomVersion = ${it.name}EntityVersion")
                                            .apply {
                                                val getter = it.getter
                                                val setter = it.setter
                                                addStatement(
                                                    "entity.${setter.name}(${clazz.classSimpleName.charLower()}.${getter.name}())"
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
                                                        val keyGetter =
                                                            clazz.roomPrimaryKeyField().getter
                                                        addStatement(
                                                            "\$T.${clazz.classSimpleName.charLower()}().update${it.name.charUpper()}(" +
                                                                    "${clazz.classSimpleName.charLower()}.${keyGetter.name}() ," +
                                                                    "${clazz.classSimpleName.charLower()}.${getter.name}()" +
                                                                    ")",
                                                            dbRoomClassName()
                                                        )
                                                    }
                                                    .addStatement("List<WeakReference<${clazz.classSimpleName}>> ${clazz.classSimpleName.charLower()}OfRoom = ${clazz.classSimpleName.charLower()}OfAll()")
                                                    .beginControlFlow("for (WeakReference<${clazz.classSimpleName}> reference : ${clazz.classSimpleName.charLower()}OfRoom)")
                                                    .addStatement("${clazz.classSimpleName} entity = reference.get()")
                                                    .addStatement("if (entity == null) continue")
                                                    .addStatement(
                                                        "\$T observer = from(entity)",
                                                        clazz.observerClassName().asTypeName()
                                                    )
                                                    .beginControlFlow("if (${it.name}EntityVersion > observer.${it.name}EntityVersion)")
                                                    .addStatement("observer.${it.name}EntityVersion = ${it.name}EntityVersion - 1")
                                                    .addStatement("observer.${it.name}RoomVersion = ${it.name}EntityVersion")
                                                    .apply {
                                                        val getter = it.getter
                                                        val setter = it.setter
                                                        addStatement(
                                                            "entity.${setter.name}(${clazz.classSimpleName.charLower()}.${getter.name}())"
                                                        )
                                                    }
                                                    .endControlFlow()
                                                    .endControlFlow()

                                                    .endControlFlow()
                                                    .endControlFlow()
                                                    .unindent()
                                                    .addStatement("})")

                                                    .build()
                                            )
                                    }
                                    .build()
                            }
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                clazz.asTypeName(),
                                clazz.classSimpleName.charLower()
                            )
                            .addStatement("this.${clazz.classSimpleName.charLower()} = ${clazz.classSimpleName.charLower()}")
                            .addStatement("all.add(new WeakReference(${clazz.classSimpleName.charLower()}))")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("cleanCache")
                            .addModifiers(Modifier.PRIVATE)
                            .addStatement("int size = all.size()")
                            .beginControlFlow("for (int index = size - 1; index >= 0; index--)")
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
                                    clazz.asTypeName(), "entity"
                                ).build()
                            )
                            .returns(clazz.observerClassName().asTypeName())
                            .addStatement(
                                "\$T roomObservable = null",
                                JavaClass.roomObservable
                            )
                            .addStatement("Object target = entity")
                            .beginControlFlow("if (target instanceof RoomObservable)")
                            .addStatement("roomObservable = (RoomObservable) target")
                            .endControlFlow()
                            .addStatement("if (roomObservable == null) return null")
                            .addStatement("return (${clazz.classSimpleName}Observable) roomObservable.getDbObservable()")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("${clazz.classSimpleName.charLower()}OfAll")
                            .returns(
                                ParameterizedTypeName.get(
                                    ClassName.get(List::class.java),
                                    ParameterizedTypeName.get(
                                        ClassName.get(WeakReference::class.java),
                                        clazz.asTypeName()
                                    )
                                )
                            )
                            .addStatement("return new ArrayList(all)")
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }

    fun dbClassName() = ClassName.get("com.iwdael.dbroom", "DB")
    fun dbRoomClassName() = ClassName.get("com.iwdael.dbroom", "DbRoom")
}