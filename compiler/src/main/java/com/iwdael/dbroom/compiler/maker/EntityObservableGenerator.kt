package com.iwdael.dbroom.compiler.maker

import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickModifier
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickParameter
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickReturn
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.ROOM_NOTIFIER
import com.iwdael.dbroom.compiler.JavaClass.ROOM_NOTIFIER_NOTIFIER
import com.iwdael.dbroom.compiler.JavaClass.debug
import com.iwdael.dbroom.compiler.compat.*
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
    override val simpleClassNameGen: String = clazz.observerClassName().simpleName()
    override val packageNameGen: String = clazz.observerClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    private val observable = ClassName.bestGuess("androidx.databinding.Observable")
    override fun generate(filer: Filer) {
        val useDataBinding = clazz.useDataBinding()
        val useRoomNotifier = clazz.useRoomNotifier()
        JavaFile
            .builder(
                packageNameGen,
                TypeSpec.classBuilder(simpleClassNameGen)
                    .superclass(clazz.asTypeName())
                    .addModifiers(Modifier.PUBLIC)
                    .apply {
                        if (useDataBinding)
                            addSuperinterface(observable)
                    }
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
                        if (useRoomNotifier) {
                            clazz.roomFields()
                                .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                .forEach {
                                    addField(
                                        FieldSpec.builder(TypeName.INT, "${it.name}RoomVersion")
                                            .addModifiers(Modifier.PRIVATE)
                                            .initializer("-1")
                                            .build()
                                    )
                                    addField(
                                        FieldSpec.builder(TypeName.INT, "${it.name}EntityVersion")
                                            .addModifiers(Modifier.PRIVATE)
                                            .initializer("-1")
                                            .build()
                                    )
                                }
                        }
                    }
                    .apply {
                        if (useRoomNotifier) {
                            val weak = ParameterizedTypeName.get(
                                ClassName.get(WeakReference::class.java),
                                clazz.observerClassName()
                            )
                            val list =
                                ParameterizedTypeName.get(ClassName.get(List::class.java), weak)
                            addField(
                                FieldSpec.builder(
                                    list,
                                    "${clazz.classSimpleName.charLower()}Container"
                                )
                                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                    .initializer(
                                        "new \$T<>()",
                                        ClassName.get(ArrayList::class.java)
                                    )
                                    .build()
                            )
                        }
                    }
                    .apply {
                        if (useRoomNotifier) {
                            addFields(
                                clazz.fields
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .map {
                                        FieldSpec.builder(
                                            "java.lang.Object".bestGuessClassName(),
                                            "${it.name}Lock",
                                            Modifier.STATIC,
                                            Modifier.FINAL,
                                            Modifier.PRIVATE
                                        )
                                            .initializer("new Object()")
                                            .build()
                                    }
                            )
                        }
                    }
                    .addMethod(
                        MethodSpec
                            .methodBuilder("notifyPropertyChanged")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                ParameterSpec.builder(
                                    TypeName.INT, "fieldId"
                                ).build()
                            )
                            .apply {
                                if (useRoomNotifier) {
                                    addStatement("cleanCache()")
                                }
                            }
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
                                                "callbacks.notifyCallbacks(this, \$T.${it.name.charLower()}, null)",
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
                                        this.beginControlFlow("if (this.${clazz.roomPrimaryKeyField().getter.name}() == null)")
                                            .apply {
                                                if (debug)
                                                    addStatement(
                                                        "\$T.w(\"DbRoom\", \"there is not ${clazz.roomPrimaryKeyField().name} in ${clazz.classSimpleName}\")",
                                                        JavaClass.LOGGER
                                                    )
                                            }
                                            .addStatement("return")
                                            .endControlFlow()
                                            .beginControlFlow("synchronized (${it.name}Lock)")
                                            .beginControlFlow("if (${it.name}EntityVersion == -1)")
                                            .addStatement("int maxVersion = 0")
                                            .addStatement(
                                                "\$T ${clazz.classSimpleName.charLower()}OfInit " +
                                                        "= ${clazz.classSimpleName.charLower()}OfAll()",
                                                listWeakObservable()
                                            )
                                            .beginControlFlow(
                                                "for (\$T reference : ${clazz.classSimpleName.charLower()}OfInit)",
                                                weakObservable()
                                            )
                                            .addStatement(
                                                "\$T entity = reference.get()",
                                                clazz.observerClassName()
                                            )
                                            .addStatement("if (entity == null) continue")
                                            .addStatement("maxVersion = Math.max(entity.${it.name}EntityVersion, maxVersion)")
                                            .endControlFlow()
                                            .addStatement("${it.name}EntityVersion = maxVersion")
                                            .addStatement("return")
                                            .endControlFlow()
                                            .addStatement("${it.name}EntityVersion++")
                                            .addStatement(
                                                "\$T ${clazz.classSimpleName.charLower()}OfAll " +
                                                        "= ${clazz.classSimpleName.charLower()}OfAll()",
                                                listWeakObservable()
                                            )
                                            .beginControlFlow(
                                                "for (\$T reference : ${clazz.classSimpleName.charLower()}OfAll)",
                                                weakObservable()
                                            )
                                            .addStatement(
                                                "\$T entity = reference.get()",
                                                clazz.observerClassName()
                                            )
                                            .addStatement("if (entity == null) continue")
                                            .beginControlFlow("if (${it.name}EntityVersion > entity.${it.name}EntityVersion)")
                                            .addStatement("entity.${it.name}EntityVersion = ${it.name}EntityVersion - 1")
                                            .addStatement("entity.${it.name}RoomVersion = ${it.name}EntityVersion")
                                            .apply {
                                                val getter = it.getter
                                                val setter = it.setter
                                                addStatement(
                                                    "entity.${setter.name}(this.${getter.name}())"
                                                )
                                            }
                                            .endControlFlow()
                                            .endControlFlow()

                                            .addStatement("if (${it.name}RoomVersion >= ${it.name}EntityVersion) return")
                                            .addStatement("${it.name}RoomVersion = ${it.name}EntityVersion - 1")
                                            .addCode(
                                                CodeBlock.builder()
                                                    .beginControlFlow(
                                                        "\$T.notifyRoom(new \$T()",
                                                        ROOM_NOTIFIER,
                                                        ROOM_NOTIFIER_NOTIFIER
                                                    )
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
                                                                    "\$T.this.${keyGetter.name}() ," +
                                                                    "\$T.this.${getter.name}()" +
                                                                    ")",
                                                            dbRoomClassName(),
                                                            clazz.observerClassName(),
                                                            clazz.observerClassName()
                                                        )
                                                    }
                                                    .addStatement(
                                                        "\$T ${clazz.classSimpleName.charLower()}OfRoom = ${clazz.classSimpleName.charLower()}OfAll()",
                                                        listWeakObservable()
                                                    )
                                                    .beginControlFlow(
                                                        "for (\$T reference : ${clazz.classSimpleName.charLower()}OfRoom)",
                                                        weakObservable()
                                                    )
                                                    .addStatement(
                                                        "\$T entity = reference.get()",
                                                        clazz.observerClassName()
                                                    )
                                                    .addStatement("if (entity == null) continue")
                                                    .beginControlFlow("if (${it.name}EntityVersion > entity.${it.name}EntityVersion)")
                                                    .addStatement("entity.${it.name}EntityVersion = ${it.name}EntityVersion - 1")
                                                    .addStatement("entity.${it.name}RoomVersion = ${it.name}EntityVersion")
                                                    .apply {
                                                        val getter = it.getter
                                                        val setter = it.setter
                                                        addStatement(
                                                            "entity.${setter.name}(\$T.this.${getter.name}())",
                                                            clazz.observerClassName()
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
                                            .endControlFlow()
                                    }
                                    .build()
                            }
                    )
                    .apply {
                        if (useDataBinding || useRoomNotifier) {
                            addMethods(
                                clazz.fields
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .map {
                                        it.setter.asMethodBuilder()
                                            .stickParameter()
                                            .stickModifier()
                                            .addAnnotation(Bindable::class.java)
                                            .addStatement("super.${it.setter.name}(${it.setter.parameter.first().name})")
                                            .addStatement("notifyPropertyChanged(DB.${it.name})")
                                            .build()
                                    }
                            )
                        }
                    }
                    .apply {
                        if (useDataBinding) {
                            addMethods(
                                clazz.fields
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .map {
                                        it.getter.asMethodBuilder()
                                            .stickParameter()
                                            .stickModifier()
                                            .stickReturn()
                                            .addAnnotation(Bindable::class.java)
                                            .addStatement("return super.${it.getter.name}()")
                                            .build()
                                    }
                            )
                        }
                    }
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PRIVATE)
                            .apply {
                                if (useRoomNotifier) {
                                    addStatement("${clazz.classSimpleName.charLower()}Container.add(new WeakReference(this))")
                                }
                            }
                            .build()
                    )
                    .apply {
                        if (useRoomNotifier) {
                            addMethod(
                                MethodSpec.methodBuilder("cleanCache")
                                    .addModifiers(Modifier.PRIVATE)
                                    .addStatement("int size = ${clazz.classSimpleName.charLower()}Container.size()")
                                    .beginControlFlow("for (int index = size - 1; index >= 0; index--)")
                                    .beginControlFlow("if (null == ${clazz.classSimpleName.charLower()}Container.get(index).get())")
                                    .addStatement("${clazz.classSimpleName.charLower()}Container.remove(index)")
                                    .endControlFlow()
                                    .endControlFlow()
                                    .build()
                            )
                            addMethod(
                                MethodSpec.methodBuilder("${clazz.classSimpleName.charLower()}OfAll")
                                    .returns(
                                        ParameterizedTypeName.get(
                                            ClassName.get(List::class.java),
                                            ParameterizedTypeName.get(
                                                ClassName.get(WeakReference::class.java),
                                                clazz.observerClassName()
                                            )
                                        )
                                    )
                                    .addStatement("return new ArrayList(${clazz.classSimpleName.charLower()}Container)")
                                    .build()
                            )
                        }
                    }
                    .addMethod(
                        MethodSpec.methodBuilder("notifyPropertiesChange")
                            .addModifiers(Modifier.PUBLIC)
                            .apply {
                                if (useRoomNotifier) {
                                    beginControlFlow("if (this.${clazz.roomPrimaryKeyField().getter.name}() == null)")
                                        .apply {
                                            if (debug)
                                                addStatement(
                                                    "\$T.w(\"DbRoom\", \"there is not ${clazz.roomPrimaryKeyField().name} in ${clazz.classSimpleName}\")",
                                                    JavaClass.LOGGER
                                                )
                                        }
                                        .addStatement("return")
                                        .endControlFlow()
                                }
                                clazz.roomFields()
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .forEach { it ->
                                        if (useRoomNotifier) {
                                            this.beginControlFlow("if (${it.name}EntityVersion == -1)")
                                                .addStatement("int maxVersion = 0")
                                                .addStatement(
                                                    "\$T ${clazz.classSimpleName.charLower()}OfInit = ${clazz.classSimpleName.charLower()}OfAll()",
                                                    listWeakObservable()
                                                )
                                                .beginControlFlow(
                                                    "for (\$T reference : ${clazz.classSimpleName.charLower()}OfInit)",
                                                    weakObservable()
                                                )
                                                .addStatement(
                                                    "\$T entity = reference.get()",
                                                    clazz.observerClassName()
                                                )
                                                .addStatement("if (entity == null) continue")
                                                .addStatement("maxVersion = Math.max(entity.${it.name}EntityVersion, maxVersion)")
                                                .endControlFlow()
                                                .addStatement("${it.name}EntityVersion = maxVersion")
                                                .endControlFlow()
                                        }
                                        addStatement("notifyPropertyChanged(DB.${it.name})")
                                    }
                            }
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("from")
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addParameter(clazz.asTypeName(), clazz.classSimpleName.charLower())
                            .returns(clazz.observerClassName())
                            .beginControlFlow("if (${clazz.classSimpleName.charLower()} == null)")
                            .addStatement("return null")
                            .endControlFlow()
                            .addStatement(
                                "\$T observable = new \$T()",
                                clazz.observerClassName(),
                                clazz.observerClassName()
                            )
                            .apply {
                                clazz.fields.forEach {
                                    addStatement("observable.${it.setter.name}(${clazz.classSimpleName.charLower()}.${it.getter.name}())")
                                }
                            }
                            .addStatement("return observable")
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }

    private fun dbClassName() = ClassName.get("com.iwdael.dbroom", "DB")
    private fun dbRoomClassName() = ClassName.get("com.iwdael.dbroom", "DbRoom")

    private fun listWeakObservable() = ParameterizedTypeName.get(
        ClassName.get(List::class.java),
        ParameterizedTypeName.get(
            ClassName.get(WeakReference::class.java),
            clazz.observerClassName()
        )
    )

    private fun weakObservable() = ParameterizedTypeName.get(
        ClassName.get(WeakReference::class.java),
        clazz.observerClassName()
    )
}