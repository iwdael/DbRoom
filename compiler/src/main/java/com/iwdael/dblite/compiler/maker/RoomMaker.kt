package com.iwdael.dblite.compiler.maker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import com.iwdael.dblite.compiler.DTA
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


class RoomMaker(private val dta: DTA) : Maker {
    override fun classFull() = "${Maker.ROOT_PACKAGE}.${dta.targetClassName}"
    override fun className() = "${dta.targetClassName}Room"
    override fun packageName() = Maker.ROOT_PACKAGE
    override fun make(filer: Filer) {
        val find = MethodSpec.methodBuilder("find")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${dta.tableName} WHERE ${
                            dta.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        }\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
                    }
            }
            .build()

        val findOrderDesc = MethodSpec.methodBuilder("findDesc")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${dta.tableName} WHERE ${
                            dta.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName DESC\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
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
                        "value", "\"SELECT * FROM ${dta.tableName} WHERE ${
                            dta.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName ASC\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
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
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .addStatement("return asc ? " +
                    "findAsc(${
                        dta.eClass.getVariable().map { it.name() }
                            .joinToString(separator = ", ", postfix = ", columnName.name")
                    }) : " +
                    "findDesc(${
                        dta.eClass.getVariable().map { it.name() }
                            .joinToString(separator = ", ", postfix = ", columnName.name")
                    })"
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
                    }
                addParameter(
                    ParameterSpec.builder(
                        ClassName.get(
                            "${Maker.ROOT_PACKAGE}.${dta.targetClassName}Db",
                            "Column"
                        ), "columnName"
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
                        "value", "\"SELECT * FROM ${dta.tableName} WHERE ${
                            dta.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        } LIMIT :offset,:size\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
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
                        "value", "\"SELECT * FROM ${dta.tableName} WHERE ${
                            dta.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName ASC LIMIT :offset,:size\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
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
                        "value", "\"SELECT * FROM ${dta.tableName} WHERE ${
                            dta.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        } ORDER BY :columnName DESC LIMIT :offset,:size\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
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
                    ClassName.get(dta.packageName, dta.targetClassName)
                )
            )
            .apply {
                dta.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
                    }
                addParameter(
                    ParameterSpec.builder(
                        ClassName.get(
                            "${Maker.ROOT_PACKAGE}.${dta.targetClassName}Db",
                            "Column"
                        ), "columnName"
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
                        dta.eClass.getVariable().map { it.name() }
                            .joinToString(
                                separator = ", ",
                                postfix = ", columnName.name, offset, size"
                            )
                    }) : " +
                    "findDesc(${
                        dta.eClass.getVariable().map { it.name() }
                            .joinToString(
                                separator = ", ",
                                postfix = ", columnName.name, offset, size"
                            )
                    })"
            )
            .build()

        val insert = MethodSpec.methodBuilder("insert")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Insert::class.java)
            .addParameter(
                ArrayTypeName.of(ClassName.get(dta.packageName, dta.targetClassName)),
                "entity"
            )
            .varargs(true)
            .build()

        val all = MethodSpec.methodBuilder("all")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember("value", "\"SELECT * FROM ${dta.tableName}\"")
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(dta.packageName, dta.targetClassName)
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
                    ClassName.get(dta.packageName, dta.targetClassName)
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
            .writeTo(filer)
    }


}