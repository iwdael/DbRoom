package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.annotations.UseFlow
import com.iwdael.dbroom.compiler.compat.*
import com.iwdael.dbroom.compiler.packageName
import com.iwdael.dbroom.compiler.roomFields
import com.iwdael.dbroom.compiler.roomPackage
import com.iwdael.dbroom.compiler.roomTableName
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class EntityRoomGenerator(private val clazz: Class) : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName() = "${clazz.classSimpleName}Room"
    override fun packageName() = clazz.roomPackage()


    private fun replaceArray() = MethodSpec.methodBuilder("replace")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Insert::class.java)
                .addMember("entity", "${clazz.classSimpleName}.class")
                .addMember(
                    "onConflict",
                    "\$T.REPLACE",
                    ClassName.get("androidx.room", "OnConflictStrategy")
                )
                .build()
        )
        .addParameter(
            ArrayTypeName.of(ClassName.get(clazz.packageName(), clazz.classSimpleName)),
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
                        "\"INSERT OR REPLACE INTO ${clazz.roomTableName()}(${
                            clazz.roomFields().joinToString(
                                separator = " ,",
                                transform = { it.colName() })
                        }) values(${
                            clazz.roomFields().joinToString(
                                separator = " ,",
                                transform = { ":${it.name}" })
                        })\""
                    )
                    .build()
            )
            .addParameters(clazz.roomFields().map {
                ParameterSpec.builder(
                    it.asTypeName(),
                    it.name
                ).build()
            })
            .build()
    }

    private fun insertArray() = MethodSpec.methodBuilder("insert")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Insert::class.java)
                .addMember("entity", "${clazz.classSimpleName}.class")
                .build()
        )
        .addParameter(
            ArrayTypeName.of(clazz.asTypeName()),
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
                        "\"INSERT INTO ${clazz.roomTableName()}(${
                            clazz.roomFields().joinToString(
                                separator = " ,",
                                transform = { it.colName() })
                        }) values(${
                            clazz.roomFields().joinToString(
                                separator = " ,",
                                transform = { ":${it.name}" })
                        })\""
                    )
                    .build()
            )
            .addParameters(clazz.roomFields().map {
                ParameterSpec.builder(
                    it.asTypeName(),
                    it.name
                ).build()
            })
            .build()
    }

    private fun inserts() = clazz.getInsert().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"INSERT INTO ${clazz.roomTableName()}(${
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
                    it.asTypeName(),
                    it.name
                ).build()
            })
            .build()
    }

    private fun deleteArray() = MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Delete::class.java)
                .addMember("entity", "${clazz.classSimpleName}.class")
                .build()
        )
        .addParameter(
            ArrayTypeName.of(clazz.asTypeName()),
            "entity"
        )
        .varargs(true)
        .build()

    private fun deletes() = clazz.getDelete().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"DELETE FROM ${clazz.roomTableName()} WHERE ${
                            pair.second.joinToString(
                                separator = " AND ",
                                transform = { "${it.colName()} = :${it.name}" })
                        }\""
                    )
                    .build()
            )
            .addParameters(pair.second.map {
                ParameterSpec.builder(
                    it.asTypeName(),
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
                    "value", "\"DELETE FROM ${clazz.roomTableName()}\""
                )
                .build()
        )
        .build()

    private fun updateArray() = MethodSpec.methodBuilder("update")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Update::class.java)
                .addMember("entity", "${clazz.classSimpleName}.class")
                .build()
        )
        .addParameter(
            ArrayTypeName.of(clazz.asTypeName()),
            "entity"
        )
        .varargs(true)
        .returns(TypeName.INT)
        .build()

    private fun updateFiled() = clazz.getUpdateFiled().second.map { field ->
        val primary = clazz.getUpdateFiled().first
        MethodSpec.methodBuilder("update${field.name.charUpper()}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"UPDATE ${clazz.roomTableName()} SET ${field.colName()} = :${field.name} WHERE ${primary.colName()} = :${primary.name}\""
                    )
                    .build()
            )
            .addParameter(
                ParameterSpec.builder(
                    primary.asTypeName(),
                    primary.name
                ).build()
            )
            .addParameter(
                ParameterSpec.builder(
                    field.asTypeName(),
                    field.name
                ).build()
            )
            .returns(TypeName.INT)
            .build()
    }

    private fun updates() = clazz.getUpdate().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"UPDATE ${clazz.roomTableName()} SET ${
                            pair.second.first.joinToString(
                                separator = " , ",
                                transform = { "${it.colName()} = :set${it.name.charUpper()}" })
                        } WHERE ${
                            pair.second.second.joinToString(
                                separator = " AND ",
                                transform = { "${it.colName()} = :where${it.name.charUpper()}" })
                        }\""
                    )
                    .build()
            )
            .addParameters(pair.second.first.map {
                ParameterSpec.builder(
                    it.asTypeName(),
                    "set${it.name.charUpper()}"
                ).build()
            })
            .addParameters(pair.second.second.map {
                ParameterSpec.builder(
                    it.asTypeName(),
                    "where${it.name.charUpper()}"
                ).build()
            })
            .returns(TypeName.INT)
            .build()
    }


    private fun findAll() = MethodSpec.methodBuilder("findAll")
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember("value", "\"SELECT * FROM ${clazz.roomTableName()}\"")
                .build()
        )
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    clazz.asTypeName()
                )
            )
        )
        .build()

    private fun finds() = clazz.getQuery().map { pair ->
        MethodSpec.methodBuilder(pair.first)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Query::class.java)
                    .addMember(
                        "value",
                        "\"SELECT * FROM ${clazz.roomTableName()} WHERE ${
                            pair.second.joinToString(
                                separator = " AND ",
                                transform = { "${it.colName()} = :${it.name}" })
                        }\""
                    )
                    .build()
            )
            .addParameters(pair.second.map {
                ParameterSpec.builder(
                    it.asTypeName(),
                    it.name
                ).build()
            })
            .returns(
                useFlow(
                    ParameterizedTypeName.get(
                        ClassName.get("java.util", "List"),
                        clazz.asTypeName()
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
                    clazz.asTypeName()
                )
                .build()
        )
        .addParameter(ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery"), "sql")
        .returns(
            useFlow(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    clazz.asTypeName()
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
    private fun hasFlow() = clazz.getAnnotation(UseFlow::class.java) != null

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.interfaceBuilder(simpleClassName())
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