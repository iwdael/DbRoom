package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.annotation.UseFlow
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.getField
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
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

    private fun insert() = MethodSpec.methodBuilder("insert")
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

    private fun update() = MethodSpec.methodBuilder("update")
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

    private fun delete() = MethodSpec.methodBuilder("delete")
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
                    .addMethod(insert())
                    .addMethod(update())
                    .addMethod(delete())
                    .addMethod(replace())
                    .apply { if (generator.fields.size > 1) addMethod(findByKey()) }
                    .addMethod(find())
                    .addMethod(findAsc())
                    .addMethod(findDesc())
                    .addMethod(findLimit())
                    .addMethod(findLimitAsc())
                    .addMethod(findLimitDesc())
                    .addMethod(rawQuery())
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