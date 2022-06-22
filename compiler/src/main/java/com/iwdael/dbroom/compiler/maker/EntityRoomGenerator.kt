package com.iwdael.dbroom.compiler.maker

import androidx.annotation.Nullable
import androidx.room.*
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asAnnotation
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.INDENT
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
                        "\"INSERT OR REPLACE INTO ${clazz.roomTableName()} (${
                            clazz.roomFields().joinToString(
                                separator = " , ",
                                transform = { it.colName() })
                        }) VALUES (${
                            clazz.roomFields().joinToString(
                                separator = " , ",
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
                        "\"INSERT INTO ${clazz.roomTableName()} (${
                            clazz.roomFields().joinToString(
                                separator = " , ",
                                transform = { it.colName() })
                        }) VALUES (${
                            clazz.roomFields().joinToString(
                                separator = " , ",
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

    private fun insertSupportSQLiteQuery() = MethodSpec.methodBuilder("insert")
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
        .returns(TypeName.INT)
        .build()

    private fun inserter() = MethodSpec.methodBuilder("insert")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(clazz.sqlInserterClassName(), "inserter")
        .addStatement(
            "return insert(new \$T(inserter.selection, inserter.bindArgs))",
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery")
        )
        .returns(TypeName.INT)
        .build()

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

    private fun deleteSupportSQLiteQuery() = MethodSpec.methodBuilder("delete")
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
        .returns(TypeName.INT)
        .build()

    private fun deleter() = MethodSpec.methodBuilder("delete")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(clazz.sqlDeleterClassName(), "deleter")
        .addStatement(
            "return delete(new \$T(deleter.selection, deleter.bindArgs))",
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery")
        )
        .returns(TypeName.INT)
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

    private fun updateSupportSQLiteQuery() = MethodSpec.methodBuilder("update")
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
        .returns(TypeName.INT)
        .build()

    private fun updater() = MethodSpec.methodBuilder("update")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(clazz.sqlUpdaterClassName(), "updater")
        .addStatement(
            "return update(new \$T(updater.selection, updater.bindArgs))",
            ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery")
        )
        .returns(TypeName.INT)
        .build()

    private fun queryAll() = MethodSpec.methodBuilder("queryAll")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
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

    private fun findPrimary() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Nullable::class.java)
        .addParameter(clazz.primaryKey().asTypeName(), clazz.primaryKey().name.charLower())
        .returns(clazz.asTypeName())
        .apply {
            if (clazz.useRoom() || clazz.useNotifier()) {
                addStatement(
                    "return \$T.from(query(${clazz.primaryKey().name.charLower()}))",
                    clazz.notifierClassName()
                )
            } else {
                addStatement(
                    "return query(${clazz.primaryKey().name.charLower()})"
                )
            }
        }
        .build()

    private fun findAll() = MethodSpec.methodBuilder("findAll")
        .addModifiers(Modifier.PUBLIC)
        .returns(
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .apply {
            if (clazz.useRoom() || clazz.useNotifier()) {
                addStatement(
                    "return \$T.collectionConvert(queryAll(), \$T::from)",
                    UTILS,
                    clazz.notifierClassName()
                )
            } else {
                addStatement(
                    "return queryAll()"
                )
            }
        }
        .build()

    private fun query() = MethodSpec.methodBuilder("query")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
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

    private fun queryPrimary() = MethodSpec.methodBuilder("query")
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value",
                    "\"SELECT * FROM ${clazz.roomTableName()} WHERE ${clazz.primaryKey().colName()} = :${clazz.primaryKey().name.charLower()} LIMIT 1\""
                )
                .build()
        )
        .addParameter(clazz.primaryKey().asTypeName(), clazz.primaryKey().name.charLower())
        .returns(clazz.asTypeName())
        .build()


    private fun findSQL() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery"), "sql")
        .apply {
            if (clazz.useNotifier() || clazz.useRoom()) {
                addStatement(
                    "return \$T.collectionConvert(query(sql), \$T::from)",
                    UTILS,
                    clazz.notifierClassName()
                )
            } else {
                addStatement("return query(sql)")
            }
        }
        .returns(
            ParameterizedTypeName.get(
                ClassName.get("java.util", "List"),
                clazz.asTypeName()
            )
        )
        .build()


    private fun finder() = MethodSpec.methodBuilder("find")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(clazz.sqlFinderClassName(), "finder")
        .apply {
            if (clazz.useNotifier() || clazz.useRoom()) {
                addStatement(
                    "return \$T.collectionConvert(query(new \$T(finder.selection, finder.bindArgs)), \$T::from)",
                    UTILS,
                    ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery"),
                    clazz.notifierClassName()
                )
            } else {
                addStatement(
                    "return query(new \$T(finder.selection, finder.bindArgs))",
                    ClassName.get("androidx.sqlite.db", "SimpleSQLiteQuery")
                )
            }
        }
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
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.ABSTRACT)
                    .addAnnotation(Dao::class.java)
                    .addAnnotations(clazz.annotations.filter { it.asTypeName() == ClassName.get(TypeConverters::class.java) }.map { it.asAnnotation() })
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(replace())
                    .addMethod(replaceArray())
                    .addMethod(insert())
                    .addMethod(insertArray())
                    .addMethod(insertSupportSQLiteQuery())
                    .addMethod(inserter())

                    .addMethod(deleteArray())
                    .addMethod(deleteAll())
                    .addMethod(deleteSupportSQLiteQuery())
                    .addMethod(deleter())

                    .addMethod(updateArray())
                    .addMethods(updateFiled())
                    .addMethod(updateSupportSQLiteQuery())
                    .addMethod(updater())

                    .addMethod(query())
                    .addMethod(queryPrimary())
                    .addMethod(queryAll())
                    .addMethod(findPrimary())
                    .addMethod(findAll())
                    .addMethod(findSQL())
                    .addMethod(finder())
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(INDENT)
            .build()
            .write(filer)
    }


}