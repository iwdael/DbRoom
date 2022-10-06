package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.annotation.UseFlow
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.firstLetterUppercase
import com.iwdael.dbroom.compiler.compat.getField
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import java.lang.StringBuilder
import java.util.ArrayList
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class RoomMaker(private val generator: Generator) : Maker {
    override fun classFull() = "${packageName()}.${className()}"
    override fun className() = "${generator.className}Room"
    override fun packageName() = generator.packageNameGenerator

    private fun insertArray() = MethodSpec.methodBuilder("insert")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Insert::class.java)
                .addMember("entity", "${generator.className}.class")
                .build()
        )
        .addParameter(
            ArrayTypeName.of(ClassName.get(generator.packageName, generator.className)),
            "entity"
        )
        .varargs(true)
        .build()

    private fun insertParameter(): MethodSpec = MethodSpec.methodBuilder("insert")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"INSERT INTO ${generator.tableName} (${
                        generator.fields
                            .map { "${it.colName()} " }
                            .joinToString(separator = ",")
                    }) values (${
                        generator.fields.joinToString(separator = ",") { ":${it.name} " }
                    })\""
                )
                .build()
        )
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                it.name
            ).build()
        })
        .build()

    private fun deleteArray() = MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Delete::class.java)
                .addMember("entity", "${generator.className}.class")
                .build()
        )
        .addParameter(
            ArrayTypeName.of(ClassName.get(generator.packageName, generator.className)),
            "entity"
        )
        .varargs(true)
        .build()

    //
    private fun deleteParameterMatchNull() = MethodSpec.methodBuilder("deleteMatchNull")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"DELETE FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    }\""
                )
                .build()
        )
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                it.name
            ).build()
        })
        .build()

    private fun deleteParameter() = MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC)
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                it.name
            ).build()
        })
        .addStatement(
            "\$T builder = new \$T()",
            ClassName.get(StringBuilder::class.java),
            ClassName.get(StringBuilder::class.java)
        )
        .addStatement(
            "\$T params = new \$T<>()",
            ClassName.get(List::class.java),
            ClassName.get(ArrayList::class.java)
        )
        .apply {
            generator.fields.forEach {
                addStatement("if(builder.length() != 0) builder.append(\"AND\")")
                addStatement("if(null != ${it.name}) builder.append(\" ${it.colName()} = ? \")")
                addStatement("if(null != ${it.name}) params.add(${it.name})")
            }
        }
        .addStatement(
            "\$T sql = new \$T(\$S + builder.toString(), params.toArray())",
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery"),
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery"),
            "DELETE FROM ${generator.tableName} WHERE"
        )
        .addStatement("execute(sql)")
        .build()

    private fun deleteAll() = MethodSpec.methodBuilder("deleteAll")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"DELETE FROM ${generator.tableName}\""
                )
                .build()
        )
        .build()

    private fun updateArray() = MethodSpec.methodBuilder("update")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Update::class.java)
                .addMember("entity", "${generator.className}.class")
                .build()
        )
        .addParameter(
            ArrayTypeName.of(ClassName.get(generator.packageName, generator.className)),
            "entity"
        )
        .varargs(true)
        .returns(TypeName.INT)
        .build()

    private fun updateParameterMatcherNull() = MethodSpec.methodBuilder("updateMatchNull")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"UPDATE ${generator.tableName} SET ${
                        generator.fields.joinToString(separator = " ,") { "${it.colName()} = :set${it.name.firstLetterUppercase()}" }
                    } WHERE ${
                        generator.fields.joinToString(separator = " AND ") { "${it.colName()} = :where${it.name.firstLetterUppercase()}" }
                    }\""
                )
                .build()
        )
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                "set${it.name.firstLetterUppercase()}"
            ).build()
        })
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                "where${it.name.firstLetterUppercase()}"
            ).build()
        })
        .returns(TypeName.INT)
        .build()

    private fun updateParameter() = MethodSpec.methodBuilder("update")
        .addModifiers(Modifier.PUBLIC)
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                "set${it.name.firstLetterUppercase()}"
            ).build()
        })
        .addParameters(generator.fields.map {
            ParameterSpec.builder(
                ClassName.bestGuess(it.type),
                "where${it.name.firstLetterUppercase()}"
            ).build()
        })

        .addStatement(
            "\$T set = new \$T()",
            ClassName.get(StringBuilder::class.java),
            ClassName.get(StringBuilder::class.java)
        )
        .addStatement(
            "\$T where = new \$T()",
            ClassName.get(StringBuilder::class.java),
            ClassName.get(StringBuilder::class.java)
        )
        .addStatement(
            "\$T params = new \$T<>()",
            ClassName.get(List::class.java),
            ClassName.get(ArrayList::class.java)
        )
        .apply {
            generator.fields.forEach {
                addStatement("if(set.length() != 0) set.append(\",\")")
                addStatement("if(null != set${it.name.firstLetterUppercase()}) set.append(\" ${it.colName()} = ? \")")
                addStatement("if(null != set${it.name.firstLetterUppercase()}) params.add(set${it.name.firstLetterUppercase()})")
            }
        }
        .apply {
            generator.fields.forEach {
                addStatement("if(where.length() != 0) where.append(\"AND\")")
                addStatement("if(null != where${it.name.firstLetterUppercase()}) where.append(\" ${it.colName()} = ? \")")
                addStatement("if(null != where${it.name.firstLetterUppercase()}) params.add(where${it.name.firstLetterUppercase()})")
            }
        }


        .addStatement(
            "\$T sql = new \$T(\$S + set.toString() + \"WHERE\" + where.toString(), params.toArray())",
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery"),
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery"),
            "UPDATE ${generator.tableName} SET"
        )
        .addStatement("execute(sql)")

        .build()

    private fun find() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    }\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
        }
        .build()


    private fun findByKey() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .filter { it.getAnnotation(PrimaryKey::class.java) != null }
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    }\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .filter { it.getAnnotation(PrimaryKey::class.java) != null }
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
        }
        .build()

    private fun findDesc() = MethodSpec.methodBuilder("findDesc")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    } ORDER BY :columnName DESC\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder(String::class.java, "columnName")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
        }
        .build()

    private fun findAsc() = MethodSpec.methodBuilder("findAsc")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    } ORDER BY :columnName ASC\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder(String::class.java, "columnName")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
        }
        .build()

    private fun findOrder() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC)
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .addStatement("return asc ? " +
                "findAsc(${
                    generator.fields.map { it.name }
                        .joinToString(separator = ", ", postfix = ", rankColumnName.name")
                }) : " +
                "findDesc(${
                    generator.fields.map { it.name }
                        .joinToString(separator = ", ", postfix = ", rankColumnName.name")
                })"
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder(
                    ClassName.get(
                        "${generator.packageNameGenerator}.${generator.className}Db",
                        "Column"
                    ), "rankColumnName"
                )
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            addParameter(
                ClassName.BOOLEAN, "asc"
            )
        }
        .build()

    private fun findLimit() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    } LIMIT :offset,:size\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(ClassName.INT, "offset")
            addParameter(ClassName.INT, "size")
        }
        .build()

    private fun findLimitAsc() = MethodSpec.methodBuilder("findAsc")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    } ORDER BY :columnName ASC LIMIT :offset,:size\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder(String::class.java, "columnName")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            addParameter(ClassName.INT, "offset")
            addParameter(ClassName.INT, "size")
        }
        .build()

    private fun findLimitDesc() = MethodSpec.methodBuilder("findDesc")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                        generator.fields
                            .map { "${it.colName()} = :${it.name}" }
                            .joinToString(separator = " AND ")
                    } ORDER BY :columnName DESC LIMIT :offset,:size\""
                )
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder(String::class.java, "columnName")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            addParameter(ClassName.INT, "offset")
            addParameter(ClassName.INT, "size")
        }
        .build()

    private fun findLimitOrder() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC)
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .apply {
            generator.fields
                .forEach {
                    addParameter(
                        ParameterSpec
                            .builder(ClassName.bestGuess(it.type), it.name)
                            .build()
                    )
                }
            addParameter(
                ParameterSpec.builder(
                    ClassName.get(
                        "${generator.packageNameGenerator}.${generator.className}Db",
                        "Column"
                    ), "rankColumnName"
                )
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            addParameter(ClassName.BOOLEAN, "asc")
            addParameter(ClassName.INT, "offset")
            addParameter(ClassName.INT, "size")
        }
        .addStatement("return asc ? " +
                "findAsc(${
                    generator.fields.map { it.name }
                        .joinToString(
                            separator = ", ",
                            postfix = ", rankColumnName.name, offset, size"
                        )
                }) : " +
                "findDesc(${
                    generator.fields.map { it.name }
                        .joinToString(
                            separator = ", ",
                            postfix = ", rankColumnName.name, offset, size"
                        )
                })"
        )
        .build()


    private fun all() = MethodSpec.methodBuilder("all")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember("value", "\"SELECT * FROM ${generator.tableName}\"")
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .build()

    private fun replaces() = MethodSpec.methodBuilder("replace")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(
            ArrayTypeName.of(ClassName.get(generator.packageName, generator.className)),
            "entities"
        )
        .beginControlFlow(
            "for (\$T entity : entities)",
            ClassName.get(generator.packageName, generator.className)
        )
        .addStatement(
            String.format(
                "replace(%s)",
                generator.fields
                    .map { "entity.${generator.clazz.methods.getField(it).name}()" }
                    .joinToString(separator = ",")
            )
        )
        .endControlFlow()
        .varargs(true)
        .build()

    private fun replace() = MethodSpec.methodBuilder("replace")
        .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value",
                    String.format(
                        "\"REPLACE INTO ${generator.tableName} (%s) VALUES(%s)\"",
                        generator.fields.map { it.colName() }.joinToString(separator = ","),
                        generator.fields.map { ":${it.name}" }.joinToString(separator = ",")
                    )
                )
                .build()
        )
        .apply {
            generator.fields.forEach {
                addParameter(ClassName.bestGuess(it.type), it.name)
            }
        }
        .build()

    private fun rawQuery() = MethodSpec.methodBuilder("rawQuery")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(RawQuery::class.java)
                .addMember(
                    "observedEntities",
                    "{\$T.class}",
                    ClassName.get(generator.packageName, generator.className)
                )
                .build()
        )
        .addParameter(ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery"), "sql")
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
        )
        .build()

    private fun execute() = MethodSpec.methodBuilder("execute")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(RawQuery::class.java)
                .addMember(
                    "observedEntities",
                    "{\$T.class}",
                    ClassName.get(generator.packageName, generator.className)
                )
                .build()
        )
        .returns(TypeName.LONG)
        .addParameter(ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery"), "sql")
        .build()

    private fun useFlow(typeName: TypeName): TypeName {
        return if (hasFlow())
            ParameterizedTypeName.get(flow(), typeName)
        else typeName
    }

    private fun flow() = ClassName.get("kotlinx.coroutines.flow", "Flow")
    private fun hasFlow() = generator.clazz.getAnnotation(UseFlow::class.java) != null

    override fun make(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(className())
                    .addAnnotation(Dao::class.java)
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .addMethod(all())
                    .addMethod(insertArray())
                    .addMethod(insertParameter())
                    .addMethod(deleteArray())
                    .addMethod(deleteParameterMatchNull())
                    .addMethod(deleteParameter())
                    .addMethod(deleteAll())
                    .addMethod(updateArray())
                    .addMethod(updateParameterMatcherNull())
                    .addMethod(updateParameter())

                    .addMethod(replace())
                    .apply { if (generator.fields.size > 1) addMethod(findByKey()) }
                    .addMethod(find())
                    .addMethod(findAsc())
                    .addMethod(findDesc())
                    .addMethod(findLimit())
                    .addMethod(findLimitAsc())
                    .addMethod(findLimitDesc())
                    .addMethod(rawQuery())
                    .addMethod(execute())
                    .addMethod(findOrder())
                    .addMethod(findLimitOrder())
                    .addMethod(replaces())
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }


}