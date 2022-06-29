package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.colName
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
    override fun make(filer: Filer) {
        val find = MethodSpec.methodBuilder("find")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                            generator.clazz.fields
                                .map { "${it.colName()} = :${it.name}" }
                                .joinToString(separator = " AND ")
                        }\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
                                    .build()
                            )
                    }
            }
            .build()

        val findOrderDesc = MethodSpec.methodBuilder("findDesc")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                            generator.clazz.fields
                                .map { "${it.colName()} = :${it.name}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName DESC\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
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

        val findOrderASC = MethodSpec.methodBuilder("findAsc")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                            generator.clazz.fields
                                .map { "${it.colName()} = :${it.name}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName ASC\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
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


        val findOrder = MethodSpec.methodBuilder("find")
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .addStatement("return asc ? " +
                    "findAsc(${
                        generator.clazz.fields.map { it.name }
                            .joinToString(separator = ", ", postfix = ", rankColumnName.name")
                    }) : " +
                    "findDesc(${
                        generator.clazz.fields.map { it.name }
                            .joinToString(separator = ", ", postfix = ", rankColumnName.name")
                    })"
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
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

        val findLimit = MethodSpec.methodBuilder("find")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                            generator.clazz.fields
                                .map { "${it.colName()} = :${it.name}" }
                                .joinToString(separator = " AND ")
                        } LIMIT :offset,:size\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
                                    .build()
                            )
                    }
                addParameter(ClassName.INT, "offset")
                addParameter(ClassName.INT, "size")
            }
            .build()

        val findLimitAsc = MethodSpec.methodBuilder("findAsc")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                            generator.clazz.fields
                                .map { "${it.colName()} = :${it.name}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName ASC LIMIT :offset,:size\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
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


        val findLimitDesc = MethodSpec.methodBuilder("findDesc")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${generator.tableName} WHERE ${
                            generator.clazz.fields
                                .map { "${it.colName()} = :${it.name}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName DESC LIMIT :offset,:size\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
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


        val findLimitOrder = MethodSpec.methodBuilder("find")
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .apply {
                generator.clazz.fields
                    .forEach {
                        if (it.getAnnotation(PrimaryKey::class.java) == null)
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .build()
                            )
                        else
                            addParameter(
                                ParameterSpec
                                    .builder(ClassName.bestGuess(it.type), it.name)
                                    .addAnnotation(NotNull::class.java)
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
                        generator.clazz.fields.map { it.name }
                            .joinToString(
                                separator = ", ",
                                postfix = ", rankColumnName.name, offset, size"
                            )
                    }) : " +
                    "findDesc(${
                        generator.clazz.fields.map { it.name }
                            .joinToString(
                                separator = ", ",
                                postfix = ", rankColumnName.name, offset, size"
                            )
                    })"
            )
            .build()

        val insert = MethodSpec.methodBuilder("insert")
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

        val update = MethodSpec.methodBuilder("update")
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
            .build()

        val delete = MethodSpec.methodBuilder("delete")
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


        val all = MethodSpec.methodBuilder("all")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember("value", "\"SELECT * FROM ${generator.tableName}\"")
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .build()

        val rawQuery = MethodSpec.methodBuilder("rawQuery")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(RawQuery::class.java)
            .addParameter(ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery"), "sql")
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(generator.packageName, generator.className)
                )
            )
            .build()


        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(className())
                    .addAnnotation(Dao::class.java)
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .addMethod(all)
                    .addMethod(insert)
                    .addMethod(update)
                    .addMethod(delete)
                    .addMethod(find)
                    .addMethod(findOrderASC)
                    .addMethod(findOrderDesc)
                    .addMethod(findLimit)
                    .addMethod(findLimitAsc)
                    .addMethod(findLimitDesc)
                    .addMethod(rawQuery)
                    .addMethod(findOrder)
                    .addMethod(findLimitOrder)
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }


}