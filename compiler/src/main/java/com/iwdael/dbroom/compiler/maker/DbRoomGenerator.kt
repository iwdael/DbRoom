package com.iwdael.dbroom.compiler.maker

import androidx.room.Database
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.CONTEXT
import com.iwdael.dbroom.compiler.JavaClass.CONVERTER
import com.iwdael.dbroom.compiler.JavaClass.DB_ROOM
import com.iwdael.dbroom.compiler.JavaClass.ROOM_DATABASE
import com.iwdael.dbroom.compiler.JavaClass.PERSISTENCE
import com.iwdael.dbroom.compiler.JavaClass.PERSISTENCE_ROOM
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
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
                                        "instance = \$T.databaseBuilder(context.getApplicationContext(),$simpleClassNameGen.class,\"DbRoom.db\").build()",
                                        ClassName.get("androidx.room", "Room")
                                    )
                                    .build()
                            )
                        else
                            addStatement(
                                CodeBlock
                                    .builder()
                                    .add(
                                        "instance = ${method.parent.className}.${method.name}(context, $simpleClassNameGen.class)",
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

        val keep = MethodSpec.methodBuilder("keep")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addParameter(Object::class.java, "value")
            .addStatement("instance().persistence().keep(name, \$T.toString(value))", CONVERTER)
            .build()

        val acquire = MethodSpec.methodBuilder("acquire")
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
            .addStatement("Persistence persistence = instance().persistence().acquire(name)")
            .addStatement("if (persistence == null) return _default")
            .addStatement("T val = (T)\$T.toObject(persistence.value, _default.getClass())", CONVERTER)
            .addStatement("if (val == null) return _default")
            .addStatement("return val")
            .build()

        val acquire2 = MethodSpec.methodBuilder("acquire")
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
            .addStatement("Persistence persistence = instance().persistence().acquire(name)")
            .addStatement("if (persistence == null) return null")
            .addStatement("T val = (T)\$T.toObject(persistence.value, clazz)", CONVERTER)
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
                                add(fmt, *entities.map { it.asTypeName() }.toTypedArray(), PERSISTENCE)
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
            .addMethod(keep)
            .addMethod(acquire)
            .addMethod(acquire2)
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
                    MethodSpec.methodBuilder("persistence")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                        .returns(PERSISTENCE_ROOM)
                        .build()
                )
            }
            .addJavadoc(TYPE_COMMENT)
            .build()
        JavaFile
            .builder(packageNameGen, classTypeSpec)
            .addFileComment(FILE_COMMENT)
            .indent(JavaClass.INDENT)
            .build()
            .write(filer)

    }


}