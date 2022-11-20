package com.iwdael.dbroom.compiler.maker

import androidx.room.Database
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass.CONTEXT
import com.iwdael.dbroom.compiler.JavaClass.CONVERTER
import com.iwdael.dbroom.compiler.JavaClass.DB_ROOM
import com.iwdael.dbroom.compiler.JavaClass.ROOM_DATABASE
import com.iwdael.dbroom.compiler.JavaClass.STORE
import com.iwdael.dbroom.compiler.JavaClass.STORE_ROOM
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.charLower
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.roomClassName
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class DbRoomGenerator(
    private val entities: List<Class>,
    private val dao: List<Class>,
    private val method: Method?
) : Generator {
    override val simpleClassNameGen: String = DB_ROOM.simpleName()
    override val packageNameGen: String = DB_ROOM.packageName()
    override val classNameGen: String = "$packageNameGen.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        val init = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .returns(Void.TYPE)
            .addParameter(CONTEXT, "context")
            .addStatement("if (instance != null) return")
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("synchronized ($simpleClassNameGen.class)")
                    .addStatement(CodeBlock.of("if (instance != null) return"))
                    .apply {
                        if (method == null)
                            addStatement(
                                CodeBlock
                                    .builder()
                                    .add(
                                        "instance = \$T.databaseBuilder(context.getApplicationContext(),$simpleClassNameGen.class,\"lite.db\").build()",
                                        ClassName.get("androidx.room", "Room")
                                    )
                                    .build()
                            )
                        else
                            addStatement(
                                CodeBlock
                                    .builder()
                                    .add(
                                        "instance = (DbRoom)${method.parent.className}.${method.name}(context)",
                                        ClassName.get("androidx.room", "Room")
                                    )
                                    .build()
                            )
                    }

                    .endControlFlow()
                    .build()
            )
            .build()


        val instance = MethodSpec.methodBuilder("instance")
            .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
            .returns(ClassName.get(packageNameGen, simpleClassNameGen))
            .addStatement("if (instance == null) throw new RuntimeException(\"Please initialize DbRoom first\")")
            .addStatement("return instance")
            .build()

        val store = MethodSpec.methodBuilder("store")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addParameter(Object::class.java, "value")
            .addStatement("instance().store().store(name, \$T.toString(value))", CONVERTER)
            .build()

        val obtain = MethodSpec.methodBuilder("obtain")
            .addAnnotation(NotNull::class.java)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addTypeVariable(TypeVariableName.get("T"))
            .addParameter(
                TypeVariableName.get("T")
                    .annotated(
                        AnnotationSpec.builder(NotNull::class.java)
                            .build()
                    ),
                "_default"
            )
            .returns(TypeVariableName.get("T"))
            .addStatement("Store store = instance().store().obtain(name)")
            .addStatement("if (store == null) return _default")
            .addStatement("T val = (T)\$T.toObject(store.value, _default.getClass())", CONVERTER)
            .addStatement("if (val == null) return _default")
            .addStatement("return val")
            .build()

        val obtain2 = MethodSpec.methodBuilder("obtain")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addTypeVariable(TypeVariableName.get("T"))
            .addParameter(
                TypeVariableName.get("Class<T>")
                    .annotated(
                        AnnotationSpec.builder(NotNull::class.java)
                            .build()
                    ),
                "clazz"
            )
            .returns(TypeVariableName.get("T"))
            .addStatement("Store store = instance().store().obtain(name)")
            .addStatement("if (store == null) return null")
            .addStatement("T val = (T)\$T.toObject(store.value, clazz)", CONVERTER)
            .addStatement("if (val == null) return null")
            .addStatement("return val")
            .build()

        val classTypeSpec = TypeSpec.classBuilder(simpleClassNameGen)
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
                                    postfix = if (entities.isNotEmpty()) ",\$T.class}" else "\$T.class}",
                                    prefix = "{"
                                )
                                add(fmt, *entities.map { it.asTypeName() }.toTypedArray(), STORE)
                            }
                            .build()
                    )
                    .addMember("version", CodeBlock.builder().add("1").build())
                    .addMember("exportSchema", CodeBlock.of("false"))
                    .build()
            )
            .addField(
                ClassName.get(packageNameGen, simpleClassNameGen),
                "instance",
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.VOLATILE
            )
            .superclass(ROOM_DATABASE)
            .addMethod(init)
            .addMethod(instance)
            .addMethod(store)
            .addMethod(obtain)
            .addMethod(obtain2)
            .apply {
                entities.forEach {
                    addMethod(
                        MethodSpec.methodBuilder(it.classSimpleName.charLower())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(it.roomClassName().asTypeName())
                            .addStatement("return instance()._${it.classSimpleName.charLower()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("_" + it.classSimpleName.charLower())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                            .returns(it.roomClassName().asTypeName())
                            .build()
                    )
                }
                dao.forEach {
                    addMethod(
                        MethodSpec.methodBuilder(it.classSimpleName.charLower())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(it.asTypeName())
                            .addStatement("return instance()._${it.classSimpleName.charLower()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("_" + it.classSimpleName.charLower())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                            .returns(it.asTypeName())
                            .build()
                    )
                }
            }
            .apply {
                addMethod(
                    MethodSpec.methodBuilder("store")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                        .returns(STORE_ROOM)
                        .build()
                )
            }
            .build()
        JavaFile
            .builder(packageNameGen, classTypeSpec)
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)

    }


}