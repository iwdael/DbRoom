package com.iwdael.dbroom.compiler.children

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.SELECTION_CREATOR
import com.iwdael.dbroom.compiler.JavaClass.SQL_CREATOR
import com.iwdael.dbroom.compiler.JavaClass.SQL_NODE
import com.iwdael.dbroom.compiler.JavaClass.SQL_UNIT
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class NodeReplacerGenerator(private val clazz: Class, private val env: RoundEnvironment) {
    fun typeSpec(): TypeSpec {
        return TypeSpec.classBuilder(clazz.replacerClassName().simpleName())
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
            .addMethods(
                clazz.roomFields()
                    .distinctBy { it.asTypeName() }
                    .map {
                        val type = it.asTypeName().convertType(clazz, env)?.second?.returnClassName?.asTypeName()?.box() ?: it.asTypeName().box()
                        MethodSpec.methodBuilder("where")
                            .addParameter(it.columnClassName(), "column")
                            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                            .addStatement(
                                "return new \$T<CONVERT, SOURCE, TARGET, \$T<CONVERT, SOURCE, \$T, TARGET>>(target, column, call, builder, selectionCreator, call -> new \$T<>(call))",
                                it.whereClassName(env), clazz.replaceOperator(), type, clazz.replaceOperator(),
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    it.whereClassName(env),
                                    TypeVariableName.get("CONVERT"),
                                    TypeVariableName.get("SOURCE"),
                                    TypeVariableName.get("TARGET"),
                                    ParameterizedTypeName.get(
                                        clazz.replaceOperator(),
                                        TypeVariableName.get("CONVERT"),
                                        TypeVariableName.get("SOURCE"),
                                        type,
                                        TypeVariableName.get("TARGET")
                                    )
                                )
                            )
                            .build()
                    }
            )
            .build()
    }


}