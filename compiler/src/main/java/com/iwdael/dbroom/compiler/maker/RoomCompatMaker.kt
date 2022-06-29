package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.annotation.UseFlow
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.kotlin
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class RoomCompatMaker(private val generator: Generator) : Maker {
    override fun classFull() = "${Maker.ROOT_PACKAGE}.${className()}"
    override fun className() = "${generator.className}RoomCompat"
    override fun packageName() = generator.packageNameGenerator

    private fun findX() = FunSpec
        .builder("findX")
        .receiver(ClassName.bestGuess("${generator.packageNameGenerator}.${generator.className}Room"))
        .receiver(
            ClassName(
                generator.packageNameGenerator,
                "${generator.className}Room"
            )
        )
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(
                    ClassName(
                        generator.packageName,
                        generator.className
                    )
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name,
                            ClassName.bestGuess(it.type.kotlin()).copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }
        }
        .addStatement(
            "val query = %T.builder(\"${generator.tableName}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            generator.fields.forEach {
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
        .receiver(ClassName.bestGuess("${generator.packageNameGenerator}.${generator.className}Room"))
        .receiver(
            ClassName(
                generator.packageNameGenerator,
                "${generator.className}Room"
            )
        )
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(
                    ClassName(
                        generator.packageName,
                        generator.className
                    )
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name,
                            ClassName.bestGuess(it.type.kotlin()).copy(true)
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
            "val query = %T.builder(\"${generator.tableName}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            generator.fields.forEach {
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
        .receiver(ClassName.bestGuess("${generator.packageNameGenerator}.${generator.className}Room"))
        .receiver(
            ClassName(
                generator.packageNameGenerator,
                "${generator.className}Room"
            )
        )
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(
                    ClassName(
                        generator.packageName,
                        generator.className
                    )
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name,
                            ClassName.bestGuess(it.type.kotlin()).copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }

            addParameter(
                ParameterSpec.builder(
                    "column",
                    ClassName(
                        "${generator.packageNameGenerator}.${generator.className}Db",
                        "Column"
                    )
                ).build()
            )
            addParameter(
                ParameterSpec.builder("asc", Boolean::class.java).build()
            )


        }
        .addStatement(
            "val query = %T.builder(\"${generator.tableName}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            generator.fields.forEach {
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
        .receiver(ClassName.bestGuess("${generator.packageNameGenerator}.${generator.className}Room"))
        .receiver(
            ClassName(
                generator.packageNameGenerator,
                "${generator.className}Room"
            )
        )
        .returns(
            useFlow(
                ClassName("kotlin.collections", "List").parameterizedBy(
                    ClassName(
                        generator.packageName,
                        generator.className
                    )
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec.builder(
                            it.name,
                            ClassName.bestGuess(it.type.kotlin()).copy(true)
                        )
                            .defaultValue("null")
                            .build()
                    )
                }

            addParameter(
                ParameterSpec.builder(
                    "column",
                    ClassName(
                        "${generator.packageNameGenerator}.${generator.className}Db",
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
            "val query = %T.builder(\"${generator.tableName}\")",
            ClassName.bestGuess("androidx.sqlite.db.SupportSQLiteQueryBuilder")
        )
        .addStatement(
            "val selection = %T()",
            ClassName.bestGuess("java.lang.StringBuilder")
        )
        .addStatement("val bindArgs = mutableListOf<Any>()")
        .apply {
            generator.fields.forEach {
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
    private fun hasFlow() = generator.clazz.getAnnotation(UseFlow::class.java) != null
    override fun make(filer: Filer) {
        if (generator.clazz.getAnnotation(Metadata::class.java) == null) return
        FileSpec.builder(packageName(), className())
            .apply {
                addFunction(findX())
                addFunction(findLimit())
                addFunction(findOrder())
                addFunction(findLimitOrder())
            }
            .build()
            .writeTo(filer)
    }


}