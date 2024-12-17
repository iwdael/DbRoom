package com.iwdael.dbroom.compiler.generator

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.compat.smallHump
import com.iwdael.dbroom.compiler.compat.bigHump
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.getUpdateFiled
import com.iwdael.dbroom.compiler.compat.primaryKey
import com.iwdael.dbroom.compiler.compat.columns
import com.iwdael.dbroom.compiler.compat.coroutinesFunModifierCompatible
import com.iwdael.dbroom.compiler.compat.tableName
import com.iwdael.kotlinsymbolprocessor.KSPClass
import com.iwdael.kotlinsymbolprocessor.asTypeName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityRoomGenerator(private val clazz: KSPClass) : KotlinGenerator {
    override val simpleClassNameGen: String = "${clazz.simpleName}Room"
    override val packageNameGen: String = clazz.kspPackage.name
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    private fun replaceArray() = FunSpec.builder("replace")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Insert::class)
                .addMember("%L = %L", "entity", "${clazz.simpleName}::class")
                .addMember(
                    "%L = %T.REPLACE",
                    "onConflict",
                    ClassName("androidx.room", "OnConflictStrategy")
                )
                .build()
        )
        .addParameter(
            "entity",
            clazz.asTypeName(),
            KModifier.VARARG
        )
        .build()


    private fun replaceProperty(): FunSpec {
        return FunSpec.builder("replace")
            .coroutinesFunModifierCompatible()
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class)
                    .addMember(
                        "%L = %S",
                        "value",
                        "REPLACE INTO ${clazz.tableName()} (${
                            clazz.columns().joinToString(
                                separator = " , ",
                                transform = { it.colName() })
                        }) VALUES (${
                            clazz.columns().joinToString(
                                separator = " , ",
                                transform = { ":${it.name}" })
                        })"
                    )
                    .build()
            )
            .addParameters(clazz.columns().map {
                ParameterSpec.builder(
                    it.name,
                    it.type.asTypeName()
                ).build()
            })
            .build()
    }


    private fun insertArray() = FunSpec.builder("insert")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Insert::class)
                .addMember("%L = %L", "entity", "${clazz.simpleName}::class")
                .build()
        )
        .addParameter(
            "entity",
            clazz.asTypeName(),
            KModifier.VARARG
        )
        .build()

    private fun insertProperty(): FunSpec {
        return FunSpec.builder("insert")
            .coroutinesFunModifierCompatible()
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class)
                    .addMember(
                        "%L = %S",
                        "value",
                        "INSERT INTO ${clazz.tableName()} (${
                            clazz.columns().joinToString(
                                separator = " , ",
                                transform = { it.colName() })
                        }) VALUES (${
                            clazz.columns().joinToString(
                                separator = " , ",
                                transform = { ":${it.name}" })
                        })"
                    )
                    .build()
            )
            .addParameters(clazz.columns().map {
                ParameterSpec.builder(
                    it.name,
                    it.type.asTypeName(),
                ).build()
            })
            .addKdoc("return the rowid of the inserted row")
            .returns(LONG)
            .build()
    }


    private fun deleteArray() = FunSpec.builder("delete")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addKdoc("return the number of deleted rows")
        .addAnnotation(
            AnnotationSpec.builder(Delete::class)
                .addMember("%L = %L", "entity", "${clazz.simpleName}::class")
                .build()
        )
        .addParameter(
            "entity",
            clazz.asTypeName(),
            KModifier.VARARG
        )
        .build()

    private fun deleteAll() = FunSpec.builder("deleteAll")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addKdoc("return the number of deleted rows")
        .addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember(
                    "%L = %S",
                    "value", "DELETE FROM ${clazz.tableName()}"
                )
                .build()
        )
        .returns(INT)
        .build()

    private fun deleteField() = clazz.columns().map {
        FunSpec.builder("delete${it.name.bigHump()}")
            .coroutinesFunModifierCompatible()
            .addKdoc("return the number of deleted rows")
            .addModifiers(KModifier.ABSTRACT)
            .addParameter(it.name, it.type.asTypeName())
            .addAnnotation(
                AnnotationSpec.builder(Query::class)
                    .addMember(
                        "%L = %S",
                        "value", "DELETE FROM ${clazz.tableName()} WHERE ${it.colName()} = :${it.name}"
                    )
                    .build()
            )
            .returns(INT)
            .build()
    }


    private fun updateArray() = FunSpec.builder("update")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addKdoc("return the number of updated rows")
        .addAnnotation(
            AnnotationSpec.builder(Update::class)
                .addMember("%L = %L", "entity", "${clazz.simpleName}::class")
                .build()
        )
        .addParameter(
            "entity",
            clazz.asTypeName(),
            KModifier.VARARG
        )
        .returns(INT)
        .build()

    private fun updateFiled() = clazz.getUpdateFiled().second.map { field ->
        val primary = clazz.getUpdateFiled().first
        FunSpec.builder("update${field.name.bigHump()}")
            .coroutinesFunModifierCompatible()
            .addModifiers(KModifier.ABSTRACT)
            .addKdoc("return the number of updated rows")
            .addAnnotation(
                AnnotationSpec.builder(Query::class)
                    .addMember(
                        "%L = %S",
                        "value",
                        "UPDATE ${clazz.tableName()} SET ${field.colName()} = :${field.name} WHERE ${primary.colName()} = :${primary.name}"
                    )
                    .build()
            )
            .addParameter(
                ParameterSpec.builder(
                    primary.name,
                    primary.type.asTypeName().copy(false)
                ).build()
            )
            .addParameter(
                ParameterSpec.builder(
                    field.name,
                    field.type.asTypeName(),
                ).build()
            )
            .returns(INT)

            .build()
    }

    private fun findAll() = FunSpec.builder("findAll")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("%L = %S", "value", "SELECT * FROM ${clazz.tableName()}")
                .build()
        )
        .returns(
            List::class.asClassName().parameterizedBy(clazz.asTypeName())
        )
        .build()


    private fun findField() = clazz.columns().map {
        FunSpec.builder("find${it.name.bigHump()}")
            .coroutinesFunModifierCompatible()
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class)
                    .addMember(
                        "%L = %S",
                        "value",
                        "SELECT * FROM ${clazz.tableName()} WHERE ${it.colName()} = :${it.name.smallHump()}${if (it != clazz.primaryKey()) "" else " LIMIT 1"}"
                    )
                    .build()
            )
            .addParameter(it.name.smallHump(), it.type.asTypeName())
            .apply {
                if (it == clazz.primaryKey()) {
                    returns(clazz.asTypeName())
                } else {
                    returns(List::class.asClassName().parameterizedBy(clazz.asTypeName()))
                }
            }
            .build()
    }

    private fun countField() = clazz.columns().map {
        FunSpec.builder("count${it.name.bigHump()}")
            .coroutinesFunModifierCompatible()
            .addModifiers(KModifier.ABSTRACT)
            .addParameter(it.name, it.type.asTypeName())
            .addAnnotation(
                AnnotationSpec.builder(Query::class)
                    .addMember(
                        "%L = %S",
                        "value",
                        "SELECT COUNT(${it.colName()}) FROM ${clazz.tableName()} WHERE ${it.colName()} = :${it.name.smallHump()}"
                    )
                    .build()
            )
            .returns(INT)
            .build()
    }

    private fun count() = FunSpec.builder("count")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember(
                    "%L = %S",
                    "value",
                    "SELECT COUNT(${clazz.primaryKey().colName()}) FROM ${clazz.tableName()}"
                )
                .build()
        )
        .returns(INT)
        .build()

    override fun createFileSpec(): FileSpec {
        return FileSpec
            .builder(ClassName.bestGuess(classNameGen))
            .addType(
                TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(Dao::class)
                    .addAnnotations(
                        clazz.kspAnnotations
                            .filter { it.qualifierName == TypeConverters::class.qualifiedName }
                            .map {
                                it.ksp.toAnnotationSpec(true)
                            }
                            .toList()
                    )
                    .addFunction(count())
                    .addFunctions(countField())
                    .addFunction(replaceProperty())
                    .addFunction(replaceArray())

                    .addFunction(insertProperty())
                    .addFunction(insertArray())

                    .addFunction(deleteAll())
                    .addFunction(deleteArray())
                    .addFunctions(deleteField())
                    .addFunction(updateArray())
                    .addFunctions(updateFiled())
                    .addFunction(findAll())
                    .addFunctions(findField())
                    .addKdoc(TYPE_COMMENT)
                    .build(),
            )
            .addFileComment(FILE_COMMENT)
            .build()
    }


}