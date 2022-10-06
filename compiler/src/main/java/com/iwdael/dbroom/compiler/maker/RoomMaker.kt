package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.annotation.UseFlow
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.*
import com.squareup.javapoet.*
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


    private fun replaceArray() = MethodSpec.methodBuilder("replace")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Insert::class.java)
                .addMember("entity", "${generator.className}.class")
                .addMember(
                    "onConflict",
                    "\$T.REPLACE",
                    ClassName.get("androidx.room", "OnConflictStrategy")
                )
                .build()
        )
        .addParameter(
            ArrayTypeName.of(ClassName.get(generator.packageName, generator.className)),
            "entity"
        )
        .varargs(true)
        .build()

    private fun replace(): MethodSpec {
        return MethodSpec.methodBuilder("replace")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"INSERT OR REPLACE INTO ${generator.tableName}(${
                            generator.fields.joinToString(
                                separator = " ,",
                                transform = { it.colName() })
                        }) values(${
                            generator.fields.joinToString(
                                separator = " ,",
                                transform = { ":${it.name}" })
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
    }

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

    private fun insert(): MethodSpec {
        return MethodSpec.methodBuilder("insert")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"INSERT INTO ${generator.tableName}(${
                            generator.fields.joinToString(
                                separator = " ,",
                                transform = { it.colName() })
                        }) values(${
                            generator.fields.joinToString(
                                separator = " ,",
                                transform = { ":${it.name}" })
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
    }

    private fun inserts() = generator.getInsert().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"INSERT INTO ${generator.tableName}(${
                            pair.second.joinToString(
                                separator = " ,",
                                transform = { it.colName() })
                        }) values(${
                            pair.second.joinToString(
                                separator = " ,",
                                transform = { ":${it.name}" })
                        })\""
                    )
                    .build()
            )
            .addParameters(pair.second.map {
                ParameterSpec.builder(
                    ClassName.bestGuess(it.type),
                    it.name
                ).build()
            })
            .build()
    }

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

    private fun deletes() = generator.getDelete().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"DELETE FROM ${generator.tableName} WHERE ${
                            pair.second.joinToString(
                                separator = " AND ",
                                transform = { "${it.colName()} = :${it.name}" })
                        }\""
                    )
                    .build()
            )
            .addParameters(pair.second.map {
                ParameterSpec.builder(
                    ClassName.bestGuess(it.type),
                    it.name
                ).build()
            })
            .returns(TypeName.INT)
            .build()
    }

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

    private fun updateFiled() = generator.getUpdateFiled().second.map { field ->
        val primary = generator.getUpdateFiled().first
        MethodSpec.methodBuilder("update${field.name.firstLetterUppercase()}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"UPDATE ${generator.tableName} SET ${field.colName()} = :${field.name} WHERE ${primary.colName()} = :${primary.name}\""
                    )
                    .build()
            )
            .addParameter(
                ParameterSpec.builder(
                    ClassName.bestGuess(primary.type),
                    primary.name
                ).build()
            )
            .addParameter(
                ParameterSpec.builder(
                    ClassName.bestGuess(field.type),
                    field.name
                ).build()
            )
            .returns(TypeName.INT)
            .build()
    }

    private fun updates() = generator.getUpdate().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"UPDATE ${generator.tableName} SET ${
                            pair.second.first.joinToString(
                                separator = " , ",
                                transform = { "${it.colName()} = :set${it.name.firstLetterUppercase()}" })
                        } WHERE ${
                            pair.second.second.joinToString(
                                separator = " AND ",
                                transform = { "${it.colName()} = :where${it.name.firstLetterUppercase()}" })
                        }\""
                    )
                    .build()
            )
            .addParameters(pair.second.first.map {
                ParameterSpec.builder(
                    ClassName.bestGuess(it.type),
                    "set${it.name.firstLetterUppercase()}"
                ).build()
            })
            .addParameters(pair.second.second.map {
                ParameterSpec.builder(
                    ClassName.bestGuess(it.type),
                    "where${it.name.firstLetterUppercase()}"
                ).build()
            })
            .returns(TypeName.INT)
            .build()
    }


    private fun findAll() = MethodSpec.methodBuilder("findAll")
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

    private fun finds() = generator.getQuery().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"SELECT * FROM ${generator.tableName} WHERE ${
                            pair.second.joinToString(
                                separator = " AND ",
                                transform = { "${it.colName()} = :${it.name}" })
                        }\""
                    )
                    .build()
            )
            .addParameters(pair.second.map {
                ParameterSpec.builder(
                    ClassName.bestGuess(it.type),
                    it.name
                ).build()
            })
            .returns(
                useFlow(
                    ParameterizedTypeName.get(
                        ClassName.get("java.util", "List"),
                        ClassName.get(generator.packageName, generator.className)
                    )
                )
            )
            .build()
    }

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
                packageName(), TypeSpec.interfaceBuilder(className())
                    .addAnnotation(Dao::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(replace())
                    .addMethod(replaceArray())
                    .addMethod(insert())
                    .addMethod(insertArray())
                    .addMethods(inserts())
                    .addMethod(deleteArray())
                    .addMethod(deleteAll())
                    .addMethods(deletes())
                    .addMethod(updateArray())
                    .addMethods(updateFiled())
                    .addMethods(updates())
                    .addMethod(findAll())
                    .addMethods(finds())
                    .addMethod(rawQuery())
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }


}