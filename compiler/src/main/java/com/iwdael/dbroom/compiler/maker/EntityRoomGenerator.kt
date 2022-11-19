package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.UTILS
import com.iwdael.dbroom.compiler.compat.*
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityRoomGenerator(private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = "${clazz.classSimpleName}Room"
    override val packageNameGen: String = clazz.roomPackage()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
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
            ArrayTypeName.of(clazz.asTypeName()),
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
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
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
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    clazz.asTypeName()
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
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .build()

    private fun rawQuery2() = MethodSpec.methodBuilder("rawQuery")
        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
        .addAnnotation(
            AnnotationSpec.builder(RawQuery::class.java)
                .addMember(
                    "observedEntities",
                    "{\$T.class}",
                    clazz.asTypeName()
                )
                .build()
        )
        .addParameter(clazz.sqlQueryClassName(), "query")
        .addStatement(
            "return rawQuery(new \$T(query.selection, query.bindArgs))",
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery")
        )
        .returns(
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .build()

    private fun findAllNotifier() = MethodSpec.methodBuilder("findAllNotifier")
        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
        .returns(
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .addStatement(
            "return \$T.collectionConvert(findAll(), \$T::from)",
            UTILS,
            clazz.notifierClassName()
        )
        .build()

    private fun findsNotifier() = clazz.getQuery().map { pair ->
        MethodSpec.methodBuilder("${pair.first}Notifier")
            .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
            .addParameters(pair.second.map {
                ParameterSpec.builder(
                    it.asTypeName(),
                    it.name
                ).build()
            })
            .addStatement(
                "return \$T.collectionConvert(${pair.first}(${
                    pair.second.map { it.name }.joinToString(", ")
                }), \$T::from)",
                UTILS,
                clazz.notifierClassName()
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get("java.util", "List"),
                    clazz.asTypeName()
                )
            )
            .build()
    }

    private fun rawQueryNotifier() = MethodSpec.methodBuilder("rawQueryNotifier")
        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
        .addParameter(ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery"), "sql")
        .addStatement(
            "return \$T.collectionConvert(rawQuery(sql), \$T::from)",
            UTILS,
            clazz.notifierClassName()
        )
        .returns(
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .build()

    private fun rawQuery2Notifier() = MethodSpec.methodBuilder("rawQueryNotifier")
        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
        .addParameter(clazz.sqlQueryClassName(), "query")
        .addStatement(
            "return \$T.collectionConvert(rawQuery(query), \$T::from)",
            UTILS,
            clazz.notifierClassName()
        )
        .returns(
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .build()


    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.interfaceBuilder(simpleClassNameGen)
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
                    .addMethod(rawQuery2())
                    .apply {
                        val useDataBinding = clazz.useDataBinding()
                        val useRoom = clazz.useRoom()
                        val useNotify = clazz.useNotifier()
                        if (useNotify && (useDataBinding || useRoom)) {
                            this.addMethod(findAllNotifier())
                                .addMethods(findsNotifier())
                                .addMethod(rawQueryNotifier())
                                .addMethod(rawQuery2Notifier())
                        }
                    }
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}