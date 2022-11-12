package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.KotlinPoet.asTypeName
import com.iwdael.dbroom.annotations.UseFlow
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.roomClassName
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.roomPackage
import com.iwdael.dbroom.compiler.roomTableName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Filer

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityRoomCompatGenerator(private val clazz: Class) : Generator {
    override fun classFull() = "${Generator.ROOT_PACKAGE}.${simpleClassName()}"
    override fun simpleClassName() = "${clazz.classSimpleName}RoomCompat"
    override fun packageName() = clazz.roomPackage()

    private fun findX() = FunSpec
        .builder("findX")
        .receiver(clazz.roomClassName().asTypeName())
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(clazz.asTypeName())
            )
        )
        .apply {
            clazz.roomFields()
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name,
                            it.asTypeName().copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }
        }
        .addStatement(
            "val query = %T.builder(\"${clazz.roomTableName()}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            clazz.roomFields().forEach {
                beginControlFlow("${it.name}?.let")
                addStatement("if (selection.isNotEmpty()) selection.append(\" AND \")")
                addStatement(" selection.append(\"${it.colName()} = ?\")")
                addStatement("bindArgs.add(${it.name})")
                endControlFlow()
            }
        }
        .addStatement("query.selection(selection.toString(), bindArgs.toTypedArray())")
        .addStatement("return rawQuery(query.create())")
        .build()

    private fun findLimit() = FunSpec
        .builder("findX")
        .receiver(clazz.roomClassName().asTypeName())
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(clazz.asTypeName())
            )
        )
        .apply {
            clazz.roomFields()
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name,
                            it.asTypeName().copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder("offset", Int::class.java).build()
            )
            addParameter(
                ParameterSpec.builder("size", Int::class.java).build()
            )
        }
        .addStatement(
            "val query = %T.builder(\"${clazz.roomTableName()}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            clazz.roomFields().forEach {
                beginControlFlow("${it.name}?.let")
                addStatement("if (selection.isNotEmpty()) selection.append(\" AND \")")
                addStatement(" selection.append(\"${it.colName()} = ?\")")
                addStatement("bindArgs.add(${it.name})")
                endControlFlow()
            }
        }
        .addStatement("query.selection(selection.toString(), bindArgs.toTypedArray())")
        .addStatement("query.limit(\"\${offset},\${size}\")")
        .addStatement("return rawQuery(query.create())")
        .build()

    private fun findOrder() = FunSpec
        .builder("findX")
        .receiver(clazz.roomClassName().asTypeName())
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(clazz.asTypeName())
            )
        )
        .apply {
            clazz.roomFields()
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name, it.asTypeName().copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }

            addParameter(
                ParameterSpec.builder(
                    "column",
                    ClassName(
                        "${clazz.roomPackage()}.${clazz.classSimpleName}Db",
                        "Column"
                    )
                ).build()
            )
            addParameter(
                ParameterSpec.builder("asc", Boolean::class.java).build()
            )


        }
        .addStatement(
            "val query = %T.builder(\"${clazz.roomTableName()}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            clazz.roomFields().forEach {
                beginControlFlow("${it.name}?.let")
                addStatement("if (selection.isNotEmpty()) selection.append(\" AND \")")
                addStatement(" selection.append(\"${it.colName()} = ?\")")
                addStatement("bindArgs.add(${it.name})")
                endControlFlow()
            }
        }
        .addStatement("query.selection(selection.toString(), bindArgs.toTypedArray())")
        .addStatement("query.orderBy(\"\${column.name} \${if (asc) \"ASC\" else \"DESC\"})\")")
        .addStatement("return rawQuery(query.create())")
        .build()

    private fun findLimitOrder() = FunSpec
        .builder("findX")
        .receiver(clazz.roomClassName().asTypeName())
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(clazz.asTypeName())
            )
        )
        .apply {
            clazz.roomFields()
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name, it.asTypeName().copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }

            addParameter(
                ParameterSpec.builder(
                    "column",
                    ClassName(
                        "${clazz.roomPackage()}.${clazz.classSimpleName}Db",
                        "Column"
                    )
                ).build()
            )
            addParameter(
                ParameterSpec.builder("asc", Boolean::class.java).build()
            )
            addParameter(
                ParameterSpec.builder("offset", Int::class.java).build()
            )
            addParameter(
                ParameterSpec.builder("size", Int::class.java).build()
            )


        }
        .addStatement(
            "val query = %T.builder(\"${clazz.roomTableName()}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            clazz.roomFields().forEach {
                beginControlFlow("${it.name}?.let")
                addStatement("if (selection.isNotEmpty()) selection.append(\" AND \")")
                addStatement(" selection.append(\"${it.colName()} = ?\")")
                addStatement("bindArgs.add(${it.name})")
                endControlFlow()
            }
        }
        .addStatement("query.selection(selection.toString(), bindArgs.toTypedArray())")
        .addStatement("query.limit(\"\${offset},\${size}\")")
        .addStatement("query.orderBy(\"\${column.name} \${if (asc) \"ASC\" else \"DESC\"})\")")
        .addStatement("return rawQuery(query.create())")
        .build()

    private fun useFlow(typeName: TypeName): TypeName {
        return if (hasFlow())
            flow().parameterizedBy(typeName)
        else typeName
    }

    private fun flow() = ClassName("kotlinx.coroutines.flow", "Flow")
    private fun hasFlow() = clazz.getAnnotation(UseFlow::class.java) != null
    override fun generate(filer: Filer) {
        if (clazz.getAnnotation(Metadata::class.java) == null) return
        FileSpec.builder(packageName(), simpleClassName())
            .apply {
                addFunction(findX())
                addFunction(findLimit())
                addFunction(findOrder())
                addFunction(findLimitOrder())
            }
            .addComment(FILE_COMMENT)
            .build()
            .writeTo(filer)
    }


}