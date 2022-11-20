package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.COLUMN
import com.iwdael.dbroom.compiler.JavaClass.CONDITION
import com.iwdael.dbroom.compiler.JavaClass.UTILS
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntitySqlGenerator(private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = clazz.sqlClassName().simpleName()
    override val packageNameGen: String = clazz.sqlClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(
                        MethodSpec.methodBuilder("newFinder")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addStatement("return new \$T()", clazz.sqlFinderBuilderClassName())
                            .returns(clazz.sqlFinderBuilderClassName())
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("newUpdater")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addStatement("return new \$T()", clazz.sqlUpdaterBuilderClassName())
                            .returns(clazz.sqlUpdaterBuilderClassName())
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("newDeleter")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addStatement("return new \$T()", clazz.sqlDeleterBuilderClassName())
                            .returns(clazz.sqlDeleterBuilderClassName())
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("newInserter")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addStatement("return new \$T()", clazz.sqlInserterBuilderClassName())
                            .returns(clazz.sqlInserterBuilderClassName())
                            .build()
                    )
                    .addType(createSQLTypeSpec(clazz.sqlFinderClassName()))
                    .addType(createSQLTypeSpec(clazz.sqlDeleterClassName()))
                    .addType(createSQLTypeSpec(clazz.sqlUpdaterClassName()))
                    .addType(createSQLTypeSpec(clazz.sqlInserterClassName()))
                    .addType(
                        TypeSpec.classBuilder(clazz.sqlFinderBuilderClassName())
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                            .addField(
                                FieldSpec.builder(
                                    ArrayTypeName.of(clazz.interfaceColumnClassName()),
                                    "fields"
                                )
                                    .addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addField(
                                FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                        ClassName.get(List::class.java),
                                        ParameterizedTypeName.get(
                                            CONDITION,
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?")
                                        )
                                    ),
                                    "wheres"
                                )
                                    .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                                    .initializer("new \$T()", ArrayList::class.java)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("fields")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addParameter(
                                        ArrayTypeName.of(clazz.interfaceColumnClassName()),
                                        "fields"
                                    )
                                    .addStatement("this.fields = fields")
                                    .varargs()
                                    .returns(
                                        ParameterizedTypeName.get(
                                            clazz.whereBuilderClassName(),
                                            ParameterizedTypeName.get(
                                                clazz.whereBuilder2ClassName(),
                                                TypeVariableName.get("?"),
                                                clazz.sqlFinderBuilderClassName(),
                                                clazz.sqlFinderClassName()
                                            ),
                                            clazz.sqlFinderBuilderClassName(),
                                            clazz.sqlFinderClassName()
                                        )
                                    )
                                    .addStatement(
                                        "return new \$T<>(this, wheres::add, \$T::new, builder -> new \$T(builder.toSelection(), builder.toBindArgs()))",
                                        clazz.whereBuilderClassName(),
                                        clazz.whereBuilder2ClassName(),
                                        clazz.sqlFinderClassName()
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toSelection")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(String::class.java)
                                    .addStatement(
                                        "return \$T.toFinderSelection(\"${clazz.roomTableName()}\", fields, wheres)",
                                        UTILS
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toBindArgs")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(ArrayTypeName.of(Object::class.java))
                                    .addStatement("return \$T.toBindArgs(wheres)", UTILS)
                                    .build()
                            )
                            .build()
                    )
                    .addType(
                        TypeSpec.classBuilder(clazz.sqlDeleterBuilderClassName())
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                            .addField(
                                FieldSpec.builder(
                                    ArrayTypeName.of(clazz.interfaceColumnClassName()),
                                    "fields"
                                )
                                    .addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addField(
                                FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                        ClassName.get(List::class.java),
                                        ParameterizedTypeName.get(
                                            CONDITION,
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?")
                                        )
                                    ),
                                    "wheres"
                                )
                                    .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                                    .initializer("new \$T()", ArrayList::class.java)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("fields")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addParameter(
                                        ArrayTypeName.of(clazz.interfaceColumnClassName()),
                                        "fields"
                                    )
                                    .addStatement("this.fields = fields")
                                    .varargs()
                                    .returns(
                                        ParameterizedTypeName.get(
                                            clazz.whereBuilderClassName(),
                                            ParameterizedTypeName.get(
                                                clazz.whereBuilder2ClassName(),
                                                TypeVariableName.get("?"),
                                                clazz.sqlDeleterBuilderClassName(),
                                                clazz.sqlDeleterClassName()
                                            ),
                                            clazz.sqlDeleterBuilderClassName(),
                                            clazz.sqlDeleterClassName()
                                        )
                                    )
                                    .addStatement(
                                        "return new \$T<>(this, wheres::add, \$T::new, builder -> new \$T(builder.toSelection(), builder.toBindArgs()))",
                                        clazz.whereBuilderClassName(),
                                        clazz.whereBuilder2ClassName(),
                                        clazz.sqlDeleterClassName()
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toSelection")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(String::class.java)
                                    .addStatement(
                                        "return \$T.toDeleterSelection(\"${clazz.roomTableName()}\", fields, wheres)",
                                        UTILS
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toBindArgs")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(ArrayTypeName.of(Object::class.java))
                                    .addStatement("return \$T.toBindArgs(wheres)", UTILS)
                                    .build()
                            )
                            .build()
                    )

                    .addType(
                        TypeSpec.classBuilder(clazz.sqlUpdaterBuilderClassName())
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                            .addField(
                                FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                        ClassName.get(Map::class.java),
                                        ParameterizedTypeName.get(
                                            COLUMN,
                                            TypeVariableName.get("?")
                                        ),
                                        ClassName.get(Object::class.java)
                                    ), "columns"
                                )
                                    .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                                    .initializer("new \$T<>()", ClassName.get(HashMap::class.java))
                                    .build()
                            )
                            .addField(
                                FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                        ClassName.get(List::class.java),
                                        ParameterizedTypeName.get(
                                            CONDITION,
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?")
                                        )
                                    ),
                                    "wheres"
                                )
                                    .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                                    .initializer("new \$T()", ArrayList::class.java)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addMethods(
                                clazz.roomFields()
                                    .distinctBy { it.asTypeName() }
                                    .map {
                                        MethodSpec.methodBuilder("appending")
                                            .addParameter(it.columnClassName(), "column")
                                            .addParameter(it.asTypeName(), "field")
                                            .addStatement("columns.put(column, field)")
                                            .addStatement("return this")
                                            .addModifiers(Modifier.PUBLIC)
                                            .returns(clazz.sqlUpdaterBuilderClassName())
                                            .build()
                                    }
                            )
                            .addMethods(
                                clazz.roomFields()
                                    .distinctBy { it.asTypeName() }
                                    .map {
                                        MethodSpec.methodBuilder("appended")
                                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                            .addParameter(it.columnClassName(), "column")
                                            .addParameter(it.asTypeName(), "field")
                                            .addStatement("columns.put(column, field)")
                                            .returns(
                                                ParameterizedTypeName.get(
                                                    clazz.whereBuilderClassName(),
                                                    ParameterizedTypeName.get(
                                                        clazz.whereBuilder2ClassName(),
                                                        TypeVariableName.get("?"),
                                                        clazz.sqlUpdaterBuilderClassName(),
                                                        clazz.sqlUpdaterClassName()
                                                    ),
                                                    clazz.sqlUpdaterBuilderClassName(),
                                                    clazz.sqlUpdaterClassName()
                                                )
                                            )
                                            .addStatement(
                                                "return new \$T<>(this, wheres::add, \$T::new, builder -> new \$T(builder.toSelection(), builder.toBindArgs()))",
                                                clazz.whereBuilderClassName(),
                                                clazz.whereBuilder2ClassName(),
                                                clazz.sqlUpdaterClassName()
                                            )
                                            .build()
                                    }
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toSelection")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(String::class.java)
                                    .addStatement(
                                        "return \$T.toUpdaterSelection(\"${clazz.roomTableName()}\", columns, wheres)",
                                        UTILS
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toBindArgs")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(ArrayTypeName.of(Object::class.java))
                                    .addStatement("return \$T.toBindArgs(wheres)", UTILS)
                                    .build()
                            )
                            .build()
                    )

                    .addType(
                        TypeSpec.classBuilder(clazz.sqlInserterBuilderClassName())
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                            .addField(
                                FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                        ClassName.get(Map::class.java),
                                        ParameterizedTypeName.get(
                                            COLUMN,
                                            TypeVariableName.get("?")
                                        ),
                                        ClassName.get(Object::class.java)
                                    ), "columns"
                                )
                                    .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                                    .initializer("new \$T<>()", ClassName.get(HashMap::class.java))
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addMethods(
                                clazz.roomFields()
                                    .distinctBy { it.asTypeName() }
                                    .map {
                                        MethodSpec.methodBuilder("appending")
                                            .addParameter(it.columnClassName(), "column")
                                            .addParameter(it.asTypeName(), "field")
                                            .addStatement("columns.put(column, field)")
                                            .addStatement("return this")
                                            .addModifiers(Modifier.PUBLIC)
                                            .returns(clazz.sqlInserterBuilderClassName())
                                            .build()
                                    }
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("build")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addStatement(
                                        "return new \$T(toSelection(), toBindArgs())",
                                        clazz.sqlInserterClassName()
                                    )
                                    .returns(clazz.sqlInserterClassName())
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toSelection")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(String::class.java)
                                    .addStatement(
                                        "return \$T.toInserterSelection(\"${clazz.roomTableName()}\", columns)",
                                        UTILS
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toBindArgs")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(ArrayTypeName.of(Object::class.java))
                                    .addStatement("return \$T.toBindArgs(columns)", UTILS)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }

    private fun createSQLTypeSpec(clazzName: ClassName): TypeSpec {
        return TypeSpec.classBuilder(clazzName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addField(
                FieldSpec.builder(String::class.java, "selection")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .build()
            )
            .addField(
                FieldSpec.builder(ArrayTypeName.of(Object::class.java), "bindArgs")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .build()
            )
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(String::class.java, "selection")
                    .addParameter(ArrayTypeName.of(Object::class.java), "bindArgs")
                    .addStatement("this.selection = selection")
                    .addStatement("this.bindArgs = bindArgs")
                    .build()
            )
            .build()
    }
}