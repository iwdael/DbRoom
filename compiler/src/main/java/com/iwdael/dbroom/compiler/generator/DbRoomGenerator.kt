package com.iwdael.dbroom.compiler.generator

import androidx.room.Database
import com.iwdael.dbroom.annotations.DbRoomCreator
import com.iwdael.dbroom.compiler.compat.CONTEXT
import com.iwdael.dbroom.compiler.compat.CONVERTER
import com.iwdael.dbroom.compiler.compat.DB_ROOM
import com.iwdael.dbroom.compiler.compat.PERSISTENCE
import com.iwdael.dbroom.compiler.compat.PERSISTENCE_ROOM
import com.iwdael.dbroom.compiler.compat.ROOM_DATABASE
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.compat.smallHump
import com.iwdael.dbroom.compiler.compat.roomClassName
import com.iwdael.kotlinsymbolprocessor.KSPClass
import com.iwdael.kotlinsymbolprocessor.KSPFunction
import com.iwdael.kotlinsymbolprocessor.asTypeName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.annotations.NotNull

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class DbRoomGenerator(
    private val entities: List<KSPClass>,
    private val dao: List<KSPClass>,
    private val creator: KSPFunction?
) : KotlinGenerator {
    override val simpleClassNameGen: String = DB_ROOM.simpleName
    override val packageNameGen: String = DB_ROOM.packageName
    override val classNameGen: String = "$packageNameGen.${simpleClassNameGen}"
    override fun createFileSpec(): FileSpec {
        val init = FunSpec.builder("init")
            .addModifiers(KModifier.PUBLIC)
            .returns(Void.TYPE)
            .addParameter("context", CONTEXT)
            .addStatement("if (instance != null) return")
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("synchronized ($simpleClassNameGen::class)")
                    .addStatement("if (instance != null) return")
                    .apply {
                        if (creator == null)
                            addStatement(
                                "instance = %T.databaseBuilder(context.getApplicationContext(), $simpleClassNameGen::class.java, \"DbRoom.db\").build()",
                                ClassName("androidx.room", "Room")
                            )
                        else
                            addStatement(
                                "instance = ${creator.ksp.qualifiedName?.asString()}(context, $simpleClassNameGen::class.java)",
                                ClassName("androidx.room", "Room")
                            )
                    }

                    .endControlFlow()
                    .build()
            )
            .build()


        val instance = FunSpec.builder("instance")
            .addModifiers(KModifier.PRIVATE)
            .returns(ClassName(packageNameGen, simpleClassNameGen))
            .addStatement("if (instance == null) throw RuntimeException(\"Please initialize DbRoom first\")")
            .addStatement("return instance!!")
            .build()

        val keep = FunSpec.builder("keep")
            .addModifiers(KModifier.PUBLIC)
            .addParameter(
                ParameterSpec.builder("name", String::class)
                    .addAnnotation(NotNull::class)
                    .build()
            )
            .addParameter("value", Any::class.asTypeName().copy(true))
            .addStatement("instance()._persistence().keep(name, %T.toString(value))", CONVERTER)
            .build()

        val acquire = FunSpec.builder("acquire")
            .addAnnotation(NotNull::class)
            .addModifiers(KModifier.PUBLIC)
            .addParameter("name", String::class)
            .addTypeVariable(TypeVariableName("T : Any"))
            .addParameter("_default", TypeVariableName("T"))
            .returns(TypeVariableName("T"))
            .addStatement("val persistence = instance()._persistence().acquire(name)")
            .addStatement("if (persistence == null) return _default")
            .addStatement("val value = %T.toObject(persistence.value, _default.javaClass.kotlin)", CONVERTER)
            .addStatement("if (value == null) return _default")
            .addStatement("return value")
            .build()

        val acquire2 = FunSpec.builder("acquire")
            .addModifiers(KModifier.PUBLIC)
            .addParameter("name", String::class)
            .addTypeVariable(TypeVariableName("T : Any"))
            .addParameter("clazz", TypeVariableName("kotlin.reflect.KClass<T>"))
            .returns(TypeVariableName("T").copy(true))
            .addStatement("val persistence = instance()._persistence().acquire(name)")
            .addStatement("if (persistence == null) return null")
            .addStatement("val value = %T.toObject(persistence.value, clazz)", CONVERTER)
            .addStatement("if (value == null) return null")
            .addStatement("return value")
            .build()

        val objectTypeSpec = TypeSpec.companionObjectBuilder()
            .addProperty(
                PropertySpec.builder("instance", ClassName(packageNameGen, simpleClassNameGen).copy(true))
                    .mutable(true)
                    .addModifiers(KModifier.PRIVATE)
                    .addAnnotation(Volatile::class)
                    .initializer("null")
                    .build()
            )
            .addFunction(init)
            .addFunction(instance)
            .addFunction(keep)
            .addFunction(acquire)
            .addFunction(acquire2)
            .apply {
                entities.forEach {
                    addFunction(
                        FunSpec.builder(it.simpleName.smallHump())
                            .returns(ClassName.bestGuess(it.roomClassName()))
                            .addStatement("return instance()._${it.simpleName.smallHump()}()")
                            .build()
                    )
                }
                dao.forEach {
                    addFunction(
                        FunSpec.builder(it.simpleName.smallHump())
                            .returns(it.asTypeName())
                            .addStatement("return instance()._${it.simpleName.smallHump()}()")
                            .build()
                    )
                }
            }
            .build()
        val classTypeSpec = TypeSpec.classBuilder(simpleClassNameGen)
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Database::class)
                    .addMember(
                        "%L = %L",
                        "entities",
                        CodeBlock.builder()
                            .apply {
                                val fmt = entities.joinToString(
                                    separator = ", ",
                                    transform = { "%T::class" },
                                    postfix = if (entities.isNotEmpty()) ", %T::class]" else "%T::class]",
                                    prefix = "["
                                )
                                add(fmt, *entities.map { it.asTypeName() }.toTypedArray(), PERSISTENCE)
                            }
                            .build()
                    )
                    .addMember("%L = %L", "version", creator?.annotation(DbRoomCreator::class)?.version ?: 1)
                    .addMember("%L = %L", "exportSchema", creator?.annotation(DbRoomCreator::class)?.exportSchema ?: true)
                    .build()
            )
            .superclass(ROOM_DATABASE)
            .addType(objectTypeSpec)
            .apply {
                entities.forEach {

                    addFunction(
                        FunSpec.builder("_" + it.simpleName.smallHump())
                            .addModifiers(KModifier.ABSTRACT, KModifier.PROTECTED)
                            .returns(ClassName.bestGuess(it.roomClassName()))
                            .build()
                    )
                }
                dao.forEach {

                    addFunction(
                        FunSpec.builder("_" + it.simpleName.smallHump())
                            .addModifiers(KModifier.ABSTRACT, KModifier.PROTECTED)
                            .returns(it.asTypeName())
                            .build()
                    )
                }
            }
            .apply {
                addFunction(
                    FunSpec.builder("_persistence")
                        .addModifiers(KModifier.ABSTRACT, KModifier.PROTECTED)
                        .returns(PERSISTENCE_ROOM)
                        .build()
                )
            }
            .addKdoc(TYPE_COMMENT)
            .build()
        return FileSpec
            .builder(ClassName.bestGuess(classNameGen))
            .addFileComment(FILE_COMMENT)
            .addType(classTypeSpec)
            .build()
    }


}