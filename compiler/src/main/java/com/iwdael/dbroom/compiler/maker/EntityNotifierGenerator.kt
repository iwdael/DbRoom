package com.iwdael.dbroom.compiler.maker

import androidx.databinding.Bindable
import androidx.room.PrimaryKey
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asMethodBuilder
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickModifier
import com.iwdael.annotationprocessorparser.poet.JavaPoet.stickParameter
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.BASE_NOTIFIER
import com.iwdael.dbroom.compiler.JavaClass.BR
import com.iwdael.dbroom.compiler.JavaClass.DB
import com.iwdael.dbroom.compiler.JavaClass.DB_ROOM
import com.iwdael.dbroom.compiler.JavaClass.DEBUG
import com.iwdael.dbroom.compiler.JavaClass.INDENT
import com.iwdael.dbroom.compiler.JavaClass.LOGGER
import com.iwdael.dbroom.compiler.JavaClass.ROOM_NOTIFIER
import com.iwdael.dbroom.compiler.JavaClass.ROOM_NOTIFIER_NOTIFIER
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
class EntityNotifierGenerator(private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = clazz.notifierClassName().simpleName()
    override val packageNameGen: String = clazz.notifierClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    private val useDataBinding = clazz.useDataBinding()
    private val useRoom = clazz.useRoom()
    private val useNotifier = clazz.useNotifier()
    override fun generate(filer: Filer) {
        if (!clazz.useNotifier() and !clazz.useRoom()) return
        JavaFile
            .builder(
                packageNameGen,
                TypeSpec.classBuilder(simpleClassNameGen)
                    .superclass(clazz.asTypeName())
                    .apply {
                        if (useDataBinding)
                            addSuperinterface(BASE_NOTIFIER)
                    }
                    .addModifiers(Modifier.PUBLIC)
                    //Property
                    .apply {
                        if (useDataBinding) {
                            addField(
                                ClassName.get(
                                    "androidx.databinding",
                                    "PropertyChangeRegistry"
                                ), "callbacks",
                                Modifier.PRIVATE, Modifier.TRANSIENT
                            )
                        }
                        if (useNotifier && (useDataBinding || useRoom)) {
                            this
                                .addField(
                                    FieldSpec.builder(
                                        ParameterizedTypeName.get(
                                            ClassName.get(List::class.java),
                                            ParameterizedTypeName.get(
                                                ClassName.get(WeakReference::class.java),
                                                clazz.notifierClassName()
                                            )
                                        ),
                                        "${clazz.classSimpleName.charLower()}Container"
                                    )
                                        .addModifiers(
                                            Modifier.PRIVATE,
                                            Modifier.STATIC,
                                            Modifier.FINAL
                                        )
                                        .initializer(
                                            "new \$T<>()",
                                            ClassName.get(ArrayList::class.java)
                                        )
                                        .build()
                                )
                                .addFields(
                                    clazz.roomFields()
                                        .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                        .map {
                                            FieldSpec.builder(TypeName.INT, "${it.name}RoomVersion")
                                                .addModifiers(Modifier.PRIVATE)
                                                .initializer("-1")
                                                .build()
                                        }
                                )
                                .addFields(clazz.roomFields()
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .map {
                                        FieldSpec.builder(
                                            TypeName.INT,
                                            "${it.name}EntityVersion"
                                        )
                                            .addModifiers(Modifier.PRIVATE)
                                            .initializer("-1")
                                            .build()
                                    }
                                )
                                .addFields(
                                    clazz.roomFields()
                                        .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                        .map {
                                            FieldSpec.builder(
                                                ClassName.get(Object::class.java),
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
                    //constructor
                    .apply {
                        addMethod(
                            MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .apply {
                                    if (useNotifier && (useRoom || useDataBinding)) {
                                        addStatement("${clazz.classSimpleName.charLower()}Container.add(new WeakReference(this))")
                                    }
                                }
                                .build()
                        )
                    }
                    //addOnPropertyChangedCallback
                    //removeOnPropertyChangedCallback
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
                    //notifyPropertyChanged
                    .apply {
                        if (useDataBinding || useRoom) {
                            addMethod(
                                MethodSpec
                                    .methodBuilder("notifyPropertyChanged")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(
                                        ParameterSpec.builder(
                                            TypeName.INT, "fieldId"
                                        ).build()
                                    )
                                    .apply {
                                        if (useNotifier && (useRoom || useDataBinding)) {
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
                                                        "if (fieldId == \$T.${field.name})", DB
                                                    )
                                                } else {
                                                    nextControlFlow(
                                                        "else if (fieldId == \$T.${field.name})", DB
                                                    )
                                                }
                                                addStatement("notify${field.name.charUpper()}Changed()")
                                            }
                                        endControlFlow()
                                    }
                                    .build()
                            )
                        }
                    }
                    //notifyDIYChanged
                    .apply {
                        if (useNotifier && (useDataBinding || useRoom)) {
                            addMethods(notifyFieldChangeNotifier())
                        } else if (useRoom || useDataBinding) {
                            addMethods(notifyFieldChange())
                        }
                    }
                    //Setter
                    .apply {
                        if (useDataBinding || useRoom) {
                            addMethods(
                                clazz.roomFields()
                                    .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                    .filter { it.getAnnotation(Bindable::class.java) != null }
                                    .map {
                                        it.setter.asMethodBuilder()
                                            .stickParameter()
                                            .stickModifier()
                                            .addStatement("super.${it.setter.name}(${it.setter.parameter.first().name})")
                                            .addStatement(
                                                "notifyPropertyChanged(\$T.${it.name})",
                                                DB
                                            )
                                            .build()
                                    }
                            )
                        }
                    }
                    //cleanCache
                    .apply {
                        if (useNotifier && (useRoom || useDataBinding)) {
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
                                                clazz.notifierClassName()
                                            )
                                        )
                                    )
                                    .addStatement("return new ArrayList(${clazz.classSimpleName.charLower()}Container)")
                                    .build()
                            )
                        }
                    }
                    //notifyPropertiesChanged
                    .apply {
                        if (useNotifier && (useDataBinding || useRoom)) {
                            addMethod(
                                MethodSpec.methodBuilder("notifyPropertiesChanged")
                                    .addModifiers(Modifier.PUBLIC)
                                    .apply {
                                        beginControlFlow("if (this.${clazz.primaryKey().getter.name}() == null)")
                                            .apply {
                                                if (DEBUG)
                                                    addStatement(
                                                        "\$T.w(\"DbRoom\", \"there is not ${clazz.primaryKey().name} in ${clazz.classSimpleName}\")",
                                                        JavaClass.LOGGER
                                                    )
                                            }
                                            .addStatement("return")
                                            .endControlFlow()
                                        clazz.roomFields()
                                            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
                                            .forEach { it ->
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
                                                        clazz.notifierClassName()
                                                    )
                                                    .addStatement("if (entity == null) continue")
                                                    .addStatement("maxVersion = Math.max(entity.${it.name}EntityVersion, maxVersion)")
                                                    .endControlFlow()
                                                    .addStatement("${it.name}EntityVersion = maxVersion")
                                                    .endControlFlow()

                                                addStatement(
                                                    "notifyPropertyChanged(\$T.${it.name})",
                                                    DB
                                                )
                                            }

                                    }
                                    .build()
                            )
                        }
                    }
                    //from
                    .apply {
                        addMethod(
                            MethodSpec.methodBuilder("from")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .addParameter(clazz.asTypeName(), clazz.classSimpleName.charLower())
                                .returns(clazz.notifierClassName())
                                .beginControlFlow("if (${clazz.classSimpleName.charLower()} == null)")
                                .addStatement("return null")
                                .endControlFlow()
                                .addStatement(
                                    "\$T notifier = new \$T()",
                                    clazz.notifierClassName(),
                                    clazz.notifierClassName()
                                )
                                .apply {
                                    clazz.roomFields().forEach {
                                        addStatement("notifier.${it.setter.name}(${clazz.classSimpleName.charLower()}.${it.getter.name}())")
                                    }
                                }
                                .addStatement("return notifier")
                                .build()
                        )
                    }
                    //to
                    .apply {
                        addMethod(
                            MethodSpec.methodBuilder("to")
                                .addModifiers(Modifier.PUBLIC)
                                .apply {
                                    if (useDataBinding) addAnnotation(Override::class.java)
                                }
                                .returns(clazz.asTypeName())
                                .addStatement(
                                    "\$T \$N = new \$T()",
                                    clazz.asTypeName(),
                                    clazz.classSimpleName.charLower(),
                                    clazz.asTypeName()
                                )
                                .apply {
                                    clazz.roomFields().forEach {
                                        addStatement(
                                            "\$N.\$N(this.\$N())",
                                            clazz.classSimpleName.charLower(),
                                            it.setter.name,
                                            it.getter.name
                                        )
                                    }
                                }
                                .addStatement("return \$N", clazz.classSimpleName.charLower())
                                .build()
                        )
                    }
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(INDENT)
            .build()
            .write(filer)
    }

    private fun notifyFieldChange(): List<MethodSpec> {
        return clazz.roomFields()
            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
            .map {
                MethodSpec.methodBuilder("notify${it.name.charUpper()}Changed")
                    .addModifiers(Modifier.PRIVATE)
                    .apply {
                        if (useDataBinding)
                            this.beginControlFlow("synchronized (this)")
                                .beginControlFlow("if (callbacks != null)")
                                .addStatement(
                                    "callbacks.notifyCallbacks(this, \$T.${it.name}, null)",
                                    BR
                                )
                                .endControlFlow()
                                .endControlFlow()
                    }
                    .apply {
                        if (useRoom) {
                            this.addStatement(
                                "\$T.notifyRoom(\$L)",
                                ROOM_NOTIFIER,
                                TypeSpec.anonymousClassBuilder("")
                                    .addSuperinterface(ROOM_NOTIFIER_NOTIFIER)
                                    .addMethod(
                                        MethodSpec
                                            .methodBuilder("notifier")
                                            .addModifiers(Modifier.PUBLIC)
                                            .addStatement(
                                                "\$T.${clazz.classSimpleName.charLower()}().update${it.name.charUpper()}(\$T.this.${clazz.primaryKey().getter.name}(), \$T.this.${it.getter.name}())",
                                                DB_ROOM,
                                                clazz.notifierClassName(),
                                                clazz.notifierClassName()
                                            )
                                            .build()
                                    )
                                    .build()
                            )

                        }
                    }
                    .build()
            }

    }

    private fun notifyFieldChangeNotifier(): List<MethodSpec> {
        return clazz.roomFields()
            .filter { it.getAnnotation(PrimaryKey::class.java) == null }
            .map {
                MethodSpec.methodBuilder("notify${it.name.charUpper()}Changed")
                    .addModifiers(Modifier.PRIVATE)
                    .beginControlFlow("synchronized (${it.name}Lock)")
                    .apply {
                        if (DEBUG)
                            this.addStatement(
                                "\$T.v(\"DbRoom\", \$N + \$S + \$S + \$N)",
                                LOGGER,
                                "this.hashCode()",
                                ":",
                                "notify${it.name.charUpper()}Changed value: ",
                                "this.${it.getter.name}()"
                            )
                    }
                    //dataBinding
                    .apply {
                        if (useDataBinding)
                            this.beginControlFlow("if (callbacks != null)")
                                .addStatement(
                                    "callbacks.notifyCallbacks(this, \$T.${it.name}, null)",
                                    BR
                                )
                                .endControlFlow()
                    }


                    .beginControlFlow(
                        "if (this.\$N() == null || \$NEntityVersion == -1)",
                        clazz.primaryKey().getter.name,
                        it.name
                    )
                    .addStatement("\$NEntityVersion = 0", it.name)
                    .apply {
                        if (DEBUG)
                            this.addStatement(
                                "\$T.v(\"DbRoom\", \$N + \$S + \$S + \$S)",
                                LOGGER,
                                "this.hashCode()",
                                ":",
                                "notify${it.name.charUpper()}Changed state: ",
                                "init"
                            )
                    }
                    .addStatement("return")
                    .endControlFlow()

                    .beginControlFlow("if (\$NEntityVersion == 0)", it.name)
                    .addStatement("int maxVersion = 1")
                    .addStatement(
                        "\$T<\$T<\$T>> \$NOfInit = \$NOfAll()",
                        List::class.java,
                        WeakReference::class.java,
                        clazz.notifierClassName(),
                        it.name,
                        clazz.classSimpleName.charLower()
                    )
                    .beginControlFlow(
                        "for (\$T<\$T> reference : \$NOfInit)",
                        WeakReference::class.java,
                        clazz.notifierClassName(),
                        it.name
                    )
                    .addStatement("\$T notifier = reference.get()", clazz.notifierClassName())
                    .addStatement(
                        "if (notifier == null || this.\$N() != notifier.\$N()) continue",
                        clazz.primaryKey().getter.name,
                        clazz.primaryKey().getter.name
                    )
                    .addStatement(
                        "maxVersion = \$T.max(notifier.\$NEntityVersion, maxVersion)",
                        Math::class.java, it.name
                    )
                    .endControlFlow()
                    .addStatement("\$NEntityVersion = maxVersion", it.name)
                    .endControlFlow()
                    .apply {
                        if (DEBUG)
                            this.addStatement(
                                "\$T.v(\"DbRoom\", \$N + \$S + \$S + \$N)",
                                LOGGER,
                                "this.hashCode()",
                                ":",
                                "notify${it.name.charUpper()}Changed version: ",
                                "${it.name}EntityVersion"
                            )
                    }
                    .addStatement("\$NEntityVersion++", it.name)
                    .addStatement(
                        "\$T<\$T<\$T>> \$NOfAll = \$NOfAll()",
                        List::class.java,
                        WeakReference::class.java,
                        clazz.notifierClassName(),
                        clazz.classSimpleName.charLower(),
                        clazz.classSimpleName.charLower()
                    )
                    .beginControlFlow(
                        "for (\$T<\$T> reference : \$NOfAll)",
                        WeakReference::class.java,
                        clazz.notifierClassName(),
                        clazz.classSimpleName.charLower()
                    )
                    .addStatement("\$T notifier = reference.get()", clazz.notifierClassName())
                    .addStatement(
                        "if (notifier == null || this.\$N() != notifier.\$N()) continue",
                        clazz.primaryKey().getter.name,
                        clazz.primaryKey().getter.name
                    )
                    .beginControlFlow(
                        "if (\$NEntityVersion > notifier.\$NEntityVersion)",
                        it.name,
                        it.name
                    )
                    .addStatement(
                        "notifier.\$NEntityVersion = \$NEntityVersion - 1",
                        it.name,
                        it.name
                    )
                    .addStatement("notifier.\$NRoomVersion = \$NEntityVersion", it.name, it.name)
                    .addStatement("notifier.\$N(this.\$N())", it.setter.name, it.getter.name)
                    .endControlFlow()
                    .endControlFlow()
                    .apply {
                        if (!useRoom) return@apply
                        if (DEBUG) {
                            this.addStatement(
                                "\$T.v(\"DbRoom\", \$N + \$S + \$S + \$N + \$S + \$N)",
                                LOGGER,
                                "this.hashCode()",
                                ":",
                                "notify${it.name.charUpper()}Changed entityV: ",
                                "${it.name}EntityVersion",
                                " ,roomV:",
                                "${it.name}RoomVersion",
                            )
                        }
                        this
                            .addStatement(
                                "if (\$NRoomVersion >= \$NEntityVersion) return",
                                it.name,
                                it.name
                            )
                            .addStatement("\$NRoomVersion = \$NEntityVersion - 1", it.name, it.name)
                            .addStatement(
                                "\$T.notifyRoom(\$L)",
                                ROOM_NOTIFIER,
                                TypeSpec.anonymousClassBuilder("")
                                    .addSuperinterface(ROOM_NOTIFIER_NOTIFIER)
                                    .addMethod(
                                        MethodSpec.methodBuilder("notifier")
                                            .addModifiers(Modifier.PUBLIC)
                                            .addAnnotation(Override::class.java)
                                            .beginControlFlow(
                                                "if (\$NEntityVersion - \$NRoomVersion == 1)",
                                                it.name,
                                                it.name
                                            )
                                            .apply {
                                                if (DEBUG) {
                                                    this.addStatement(
                                                        "\$T.v(\"DbRoom\", \$N + \$S + \$S + \$N )",
                                                        LOGGER,
                                                        "${
                                                            clazz.notifierClassName().simpleName()
                                                        }.this.hashCode()",
                                                        ":",
                                                        "notify${it.name.charUpper()}Changed room insert: ",
                                                        "${
                                                            clazz.notifierClassName().simpleName()
                                                        }.this.${it.getter.name}()"
                                                    )
                                                }
                                            }
                                            .addStatement(
                                                "\$NRoomVersion = \$NEntityVersion",
                                                it.name,
                                                it.name
                                            )
                                            .addStatement(
                                                "\$T.\$N().update\$N(\$T.this.\$N(), \$T.this.\$N())",
                                                DB_ROOM,
                                                clazz.classSimpleName.charLower(),
                                                it.name.charUpper(),
                                                clazz.notifierClassName(),
                                                clazz.primaryKey().getter.name,
                                                clazz.notifierClassName(),
                                                it.getter.name
                                            )
                                            .addStatement(
                                                "\$T<\$T<\$T>> \$NOfRoom = \$NOfAll()",
                                                List::class.java,
                                                WeakReference::class.java,
                                                clazz.notifierClassName(),
                                                it.name,
                                                clazz.classSimpleName.charLower()
                                            )
                                            .beginControlFlow(
                                                "for (\$T<\$T> reference : \$NOfRoom)",
                                                WeakReference::class.java,
                                                clazz.notifierClassName(),
                                                it.name
                                            )
                                            .addStatement(
                                                "\$T notifier = reference.get()",
                                                clazz.notifierClassName()
                                            )
                                            .addStatement(
                                                "if (notifier == null || \$T.this.\$N() != notifier.\$N()) continue",
                                                clazz.notifierClassName(),
                                                clazz.primaryKey().getter.name,
                                                clazz.primaryKey().getter.name
                                            )
                                            .beginControlFlow(
                                                "if (\$NEntityVersion > notifier.\$NEntityVersion)",
                                                it.name,
                                                it.name
                                            )
                                            .addStatement(
                                                "notifier.\$NEntityVersion = \$NEntityVersion - 1",
                                                it.name,
                                                it.name
                                            )
                                            .addStatement(
                                                "notifier.\$NRoomVersion = \$NEntityVersion",
                                                it.name,
                                                it.name
                                            )
                                            .addStatement(
                                                "notifier.\$N(\$T.this.\$N())",
                                                it.setter.name,
                                                clazz.notifierClassName(),
                                                it.getter.name
                                            )
                                            .endControlFlow()
                                            .endControlFlow()
                                            .endControlFlow()
                                            .build()
                                    )
                                    .build()
                            )
                    }
                    .endControlFlow()//synchronized
                    .build()
            }
    }

    private fun listWeakObservable() = ParameterizedTypeName.get(
        ClassName.get(List::class.java),
        ParameterizedTypeName.get(
            ClassName.get(WeakReference::class.java),
            clazz.notifierClassName()
        )
    )

    private fun weakObservable() = ParameterizedTypeName.get(
        ClassName.get(WeakReference::class.java),
        clazz.notifierClassName()
    )
}