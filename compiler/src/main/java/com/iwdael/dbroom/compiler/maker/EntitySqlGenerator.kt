package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.COLUMN
import com.iwdael.dbroom.compiler.JavaClass.INDENT
import com.iwdael.dbroom.compiler.JavaClass.SQL_UNIT
import com.iwdael.dbroom.compiler.JavaClass.UTILS
import com.iwdael.dbroom.compiler.children.*
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntitySqlGenerator(private val env: RoundEnvironment, private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = clazz.sqlClassName().simpleName()
    override val packageNameGen: String = clazz.sqlClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addFields(columnsFiled())
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
                    .addType(columnsFieldTypeSpec())
                    .addTypes(columnsTypeSpec())
                    .addType(finderBuilder())
                    .addType(deleterBuilder())
                    .addType(updaterBuilder())
                    .addType(inserterBuilder())
                    .addTypes(clazz.roomFields()
                        .distinctBy { it.asTypeName().box() }
                        .toSet()
                        .map { orderType(it.orderClassName(), it) }
                    )
                    .addType(limitType())
                    .addType(offset())
                    .addType(unionType())
                    .addType(operator())
                    .addType(finder())
                    .addType(updater())
                    .addType(deleter())
                    .addTypes(NodeSqlUnitPackingGenerator(clazz, env).packingTypeSpec())
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(INDENT)
            .build()
            .write(filer)
    }

    private fun finderBuilder(): TypeSpec {
        return TypeSpec.classBuilder(clazz.sqlFinderBuilderClassName())
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
                            SQL_UNIT,
                            TypeVariableName.get("?"),
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
                            clazz.nodeFinderClassName(),
                            ParameterizedTypeName.get(
                                clazz.nodeFinderClassName(),
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
                        clazz.nodeFinderClassName(),
                        clazz.nodeFinderClassName(),
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
    }

    private fun deleterBuilder(): TypeSpec {
        return TypeSpec.classBuilder(clazz.sqlDeleterBuilderClassName())
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
                            SQL_UNIT,
                            TypeVariableName.get("?"),
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
                            clazz.deleterClassName(),
                            ParameterizedTypeName.get(
                                clazz.deleterClassName(),
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
                        clazz.deleterClassName(),
                        clazz.deleterClassName(),
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

    }

    private fun updaterBuilder(): TypeSpec {
        return TypeSpec.classBuilder(clazz.sqlUpdaterBuilderClassName())
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
                            SQL_UNIT,
                            TypeVariableName.get("?"),
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
                                    clazz.updaterClassName(),
                                    ParameterizedTypeName.get(
                                        clazz.updaterClassName(),
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
                                clazz.updaterClassName(),
                                clazz.updaterClassName(),
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
    }

    private fun inserterBuilder(): TypeSpec {
        return TypeSpec.classBuilder(clazz.sqlInserterBuilderClassName())
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

    private fun orderType(className: ClassName, field: Field): TypeSpec {
        return TypeSpec
            .classBuilder(className.simpleName())
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("FIELD"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .superclass(
                ParameterizedTypeName.get(
                    JavaClass.SQL_ORDER,
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("FIELD"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(SQL_UNIT, TypeVariableName.get("CONVERT"), TypeVariableName.get("SOURCE"), TypeVariableName.get("FIELD"), TypeVariableName.get("TARGET"), TypeVariableName.get("?")), "where")
                    .addStatement("super(where)")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("asc")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(
                        ParameterizedTypeName.get(
                            clazz.limitClassName(),
                            TypeVariableName.get("CONVERT"),
                            TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("FIELD"),
                            TypeVariableName.get("TARGET")
                        )
                    )
                    .addStatement("this.operator = ASC")
                    .addStatement("Utils.setWhereOrder(where, this)")
                    .addStatement("return new \$T<>(where)", clazz.limitClassName())
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("desc")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(
                        ParameterizedTypeName.get(
                            clazz.limitClassName(),
                            TypeVariableName.get("CONVERT"),
                            TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("FIELD"),
                            TypeVariableName.get("TARGET")
                        )
                    )
                    .addStatement("this.operator = DESC")
                    .addStatement("Utils.setWhereOrder(where, this)")
                    .addStatement("return new \$T<>(where)", clazz.limitClassName())
                    .build()
            )
            .build()
    }

    private fun limitType(): TypeSpec {
        return TypeSpec
            .classBuilder(clazz.limitClassName())
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("FIELD"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .superclass(
                ParameterizedTypeName.get(
                    JavaClass.SQL_LIMIT,
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("FIELD"),
                    TypeVariableName.get("TARGET"),
                )
            )
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(SQL_UNIT, TypeVariableName.get("CONVERT"), TypeVariableName.get("SOURCE"), TypeVariableName.get("FIELD"), TypeVariableName.get("TARGET"), TypeVariableName.get("?")), "where")
                    .addStatement("super(where)")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("limit")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.LONG, "limit")
                    .returns(
                        ParameterizedTypeName.get(
                            clazz.offsetClassName(),
                            TypeVariableName.get("CONVERT"),
                            TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("FIELD"),
                            TypeVariableName.get("TARGET")
                        )
                    )
                    .addStatement("this.limit = limit")
                    .addStatement("Utils.setWhereLimit(where, this)")
                    .addStatement("return new \$T<>(where)", clazz.offsetClassName())
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .returns(TypeVariableName.get("TARGET"))
                    .addStatement("return Utils.makeTarget(where)")
                    .build()
            )
            .build()
    }


    private fun unionType(): TypeSpec {
        return TypeSpec
            .classBuilder(clazz.unionClassName())
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("FIELD"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .superclass(
                ParameterizedTypeName.get(
                    JavaClass.SQL_UNION,
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("FIELD"),
                    TypeVariableName.get("TARGET"),
                )
            )
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(SQL_UNIT, TypeVariableName.get("CONVERT"), TypeVariableName.get("SOURCE"), TypeVariableName.get("FIELD"), TypeVariableName.get("TARGET"), TypeVariableName.get("?")), "where")
                    .addStatement("super(where)")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .returns(TypeVariableName.get("TARGET"))
                    .addStatement("return Utils.makeTarget(where)")
                    .build()
            )
            .build()
    }


    private fun offset(): TypeSpec {
        return TypeSpec
            .classBuilder(clazz.offsetClassName())
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("FIELD"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .superclass(
                ParameterizedTypeName.get(
                    JavaClass.SQL_OFFSET,
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("FIELD"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(SQL_UNIT, TypeVariableName.get("CONVERT"), TypeVariableName.get("SOURCE"), TypeVariableName.get("FIELD"), TypeVariableName.get("TARGET"), TypeVariableName.get("?")), "where")
                    .addStatement("super(where)")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("offset")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.LONG, "offset")
                    .returns(ParameterizedTypeName.get(clazz.unionClassName(), TypeVariableName.get("CONVERT"), TypeVariableName.get("SOURCE"), TypeVariableName.get("FIELD"), TypeVariableName.get("TARGET")))
                    .addStatement("this.offset = offset")
                    .addStatement("Utils.setWhereOffset(where, this)")
                    .addStatement("return new \$T<>(where)", clazz.unionClassName())
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .returns(TypeVariableName.get("TARGET"))
                    .addStatement("return Utils.makeTarget(where)")
                    .build()
            )
            .build()
    }

    private fun operator(): TypeSpec {
        return NodeOperatorGenerator(clazz, env).typeSpec()
    }

    private fun finder(): TypeSpec {
        return NodeFinderGenerator(clazz, env).typeSpec()
    }

    private fun deleter(): TypeSpec {
        return NodeDeleterGenerator(clazz, env).typeSpec()
    }

    private fun updater(): TypeSpec {
        return NodeUpdaterGenerator(clazz, env).typeSpec()
    }

    private fun columnsTypeSpec(): List<TypeSpec> {
        return NodeColumnGenerator(clazz, env).columnsTypeSpec()
    }

    private fun columnsFieldTypeSpec(): TypeSpec {
        return NodeColumnGenerator(clazz, env).columnsFieldTypeSpec()
    }

    private fun columnsFiled(): List<FieldSpec> {
        return NodeColumnGenerator(clazz, env).columnsFiled()
    }
}