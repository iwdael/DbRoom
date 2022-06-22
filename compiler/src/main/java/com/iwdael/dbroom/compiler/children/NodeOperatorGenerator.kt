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
class NodeOperatorGenerator(private val clazz: Class,private val env:RoundEnvironment) {
    fun typeSpec(): TypeSpec {
        return TypeSpec
            .classBuilder(clazz.findOperator())
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("FIELD"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .superclass(
                ParameterizedTypeName.get(
                    JavaClass.OPERATOR, TypeVariableName.get("CONVERT"), TypeVariableName.get("SOURCE"), TypeVariableName.get("FIELD"), TypeVariableName.get("TARGET")
                )
            )
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addMethod(
                MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(
                        ParameterSpec.builder(
                            ParameterizedTypeName.get(
                                SQL_UNIT,
                                TypeVariableName.get("CONVERT"),
                                TypeVariableName.get("SOURCE"),
                                TypeVariableName.get("FIELD"),
                                TypeVariableName.get("TARGET"),
                                TypeVariableName.get("?"),
                            ), "where"
                        )

                            .build()
                    )
                    .addStatement("super(where)")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(TypeVariableName.get("TARGET"))
                    .addStatement("return Utils.makeTarget(where)")
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
        val type = it.asTypeName().convertType(clazz, env)?.second?.returnClassName?.asTypeName()?.box()?: it.asTypeName().box()
        return MethodSpec.methodBuilder("order")
            .addParameter(it.columnClassName(), "column")
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    it.orderClassName(),
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    type,
                    TypeVariableName.get("TARGET")
                )
            )
            .addStatement(
                "return new \$T<CONVERT, SOURCE, \$T, TARGET>(Utils.createOrder(where, column))",
                it.orderClassName(), type
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
                    TypeVariableName.get("FIELD"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addStatement(
                "\$T<CONVERT, SOURCE, FIELD, TARGET> _limit = new \$T<CONVERT, SOURCE, FIELD, TARGET>(where)",
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
                    TypeVariableName.get("FIELD"),
                    TypeVariableName.get("TARGET")
                )
            )
            .addStatement(
                "\$T<CONVERT, SOURCE, FIELD, TARGET> _offset = new \$T<CONVERT, SOURCE, FIELD, TARGET>(where)",
                clazz.offsetClassName(), clazz.offsetClassName()
            )
            .addStatement("return _offset.offset(offset)")
            .build()
    }

}