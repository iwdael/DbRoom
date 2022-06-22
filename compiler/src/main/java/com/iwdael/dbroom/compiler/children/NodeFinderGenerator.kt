package com.iwdael.dbroom.compiler.children

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.SELECTION_CREATOR
import com.iwdael.dbroom.compiler.JavaClass.SQL_CREATOR
import com.iwdael.dbroom.compiler.JavaClass.SQL_NODE
import com.iwdael.dbroom.compiler.JavaClass.SQL_UNIT
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class NodeFinderGenerator(private val clazz: Class, private val env: RoundEnvironment) {
//    override val simpleClassNameGen: String = clazz.nodeFinderClassName().simpleName()
//    override val packageNameGen: String = clazz.nodeFinderClassName().packageName()
//    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"

    fun typeSpec(): TypeSpec {
        return TypeSpec.classBuilder(clazz.nodeFinderClassName().simpleName())
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .superclass(
                ParameterizedTypeName.get(
                    SQL_NODE,
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addModifiers(Modifier.PUBLIC)
            .addModifiers(Modifier.FINAL)
            .addModifiers(Modifier.STATIC)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeVariableName.get("SOURCE"), "target")
                    .addParameter(
                        ParameterizedTypeName.get(
                            CALLBACK,
                            ParameterizedTypeName.get(
                                SQL_UNIT,
                                TypeVariableName.get("CONVERT"),
                                TypeVariableName.get("SOURCE"),
                                TypeVariableName.get("?"),
                                TypeVariableName.get("TARGET"),
                                TypeVariableName.get("?")
                            )
                        ),
                        "call"
                    )
                    .addParameter(
                        ParameterizedTypeName.get(
                            SQL_CREATOR,
                            TypeVariableName.get("CONVERT"),
                            TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("TARGET")
                        ),
                        "builder"
                    )
                    .addParameter(
                        ParameterizedTypeName.get(
                            SELECTION_CREATOR, TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("TARGET")
                        ),
                        "selectionCreator"
                    )
                    .addStatement("super(target, call, builder, selectionCreator)")
                    .build()
            )
            .addMethods(clazz.roomFields()
                .distinctBy { it.asTypeName() }
                .map { where(it) }
            )
            .addMethod(
                MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(TypeVariableName.get("TARGET"))
                    .addStatement("return selectionCreator.create(target)")
                    .build()
            )
            .addMethods(
                clazz.roomFields()
                    .distinctBy { it.asTypeName().box() }
                    .map { order(it) }
            )
            .addMethod(limit())
            .addMethod(offset())
            .build()
    }

    private fun order(it: Field): MethodSpec {
        return MethodSpec.methodBuilder("order")
            .addParameter(it.columnClassName(), "column")
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    it.orderClassName(),
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    it.asTypeName().box(),
                    TypeVariableName.get("TARGET")
                )
            )
            .addStatement(
                "SqlUnit<CONVERT, SOURCE, \$T, TARGET, \$T<CONVERT, SOURCE, \$T, TARGET>> ext = new SqlUnit(target, column, call, builder, selectionCreator, null)",
                it.asTypeName().box(), clazz.limitClassName(), it.asTypeName().box()
            )
            .addStatement("this.call.call(ext)")
            .addStatement(
                "return new \$T<CONVERT, SOURCE, \$T, TARGET>(ext)",
                it.orderClassName(), it.asTypeName().box()
            )
            .build()
    }

    private fun limit(): MethodSpec {
        return MethodSpec.methodBuilder("limit")
            .addParameter(TypeName.LONG, "limit")
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    clazz.offsetClassName(),
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("Object"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addStatement(
                "SqlUnit<CONVERT, SOURCE, Object, TARGET, \$T<CONVERT, SOURCE, Object, TARGET>> ext = new SqlUnit(target, null, call, builder, selectionCreator, null)",
                clazz.limitClassName()
            )
            .addStatement("this.call.call(ext)")
            .addStatement(
                "\$T<CONVERT, SOURCE, Object, TARGET> _limit = new \$T<CONVERT, SOURCE, Object, TARGET>(ext)",
                clazz.limitClassName(), clazz.limitClassName()
            )
            .addStatement("return _limit.limit(limit)")
            .build()
    }


    private fun offset(): MethodSpec {
        return MethodSpec.methodBuilder("offset")
            .addParameter(TypeName.LONG, "offset")
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    clazz.unionClassName(),
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("Object"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addStatement(
                "SqlUnit<CONVERT, SOURCE, Object, TARGET, \$T<CONVERT, SOURCE, Object, TARGET>> ext = new SqlUnit(target, null, call, builder, selectionCreator, null)",
                clazz.offsetClassName()
            )
            .addStatement("this.call.call(ext)")
            .addStatement(
                "\$T<CONVERT, SOURCE, Object, TARGET> _offset = new \$T<CONVERT, SOURCE, Object, TARGET>(ext)",
                clazz.offsetClassName(), clazz.offsetClassName()
            )
            .addStatement("return _offset.offset(offset)")
            .build()
    }

    private fun where(it: Field): MethodSpec {
        val type = it.asTypeName().convertType(clazz, env)?.second?.returnClassName?.asTypeName()?.box() ?: it.asTypeName().box()
        return MethodSpec.methodBuilder("where")
            .addParameter(it.columnClassName(), "column")
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addStatement(
                "return new \$T<CONVERT, SOURCE, TARGET, \$T<CONVERT, SOURCE, \$T, TARGET>>(target, column, call, builder, selectionCreator, call -> new \$T<>(call))",
                it.whereClassName(env), clazz.findOperator(), type, clazz.findOperator()
            )
            .returns(
                ParameterizedTypeName.get(
                    it.whereClassName(env),
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    TypeVariableName.get("TARGET"),
                    ParameterizedTypeName.get(
                        clazz.findOperator(),
                        TypeVariableName.get("CONVERT"),
                        TypeVariableName.get("SOURCE"),
                        type,
                        TypeVariableName.get("TARGET")
                    )
                )
            )
            .build()
    }


}