package com.iwdael.dblite.compiler.maker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import com.iwdael.dblite.annotation.QuerySet
import com.iwdael.dblite.compiler.DTA
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier


class RoomMaker(private val DTA: DTA) : Maker {
    override fun classFull() = "com.iwdael.dblite.${DTA.targetClassName}"
    override fun className() = DTA.targetClassName
    override fun packageName() = "com.iwdael.dblite"
    override fun make(filer: Filer) {
        val find = MethodSpec.methodBuilder("find")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value", "\"SELECT * FROM ${DTA.tableName} WHERE ${
                            DTA.eClass.getVariable()
                                .map { "${it.colName()} = :${it.name()}" }
                                .joinToString(separator = " AND ")
                        }\""
                    )
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(DTA.packageName, DTA.targetClassName)
                )
            )
            .apply {
                DTA.eClass.getVariable()
                    .forEach {
                        addParameter(ClassName.bestGuess(it.type()), it.name())
                    }
            }
            .build()
        val insert = MethodSpec.methodBuilder("insert")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Insert::class.java)
            .addParameter(
                ArrayTypeName.of(ClassName.get(DTA.packageName, DTA.targetClassName)),
                "entity"
            )
            .varargs(true)
            .build()

        val all = MethodSpec.methodBuilder("all")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember("value", "\"SELECT * FROM ${DTA.tableName}\"")
                    .build()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    ClassName.get(DTA.packageName, DTA.targetClassName)
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
                    ClassName.get(DTA.packageName, DTA.targetClassName)
                )
            )
            .build()


        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(DTA.generatedClassName)
                    .addAnnotation(Dao::class.java)
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .addMethod(all)
                    .addMethod(insert)
                    .addMethod(find)
                    .addMethod(rawQuery)
                    .build()
            )
            .build()
            .writeTo(filer)
    }


}