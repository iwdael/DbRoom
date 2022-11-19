package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.CONDITION
import com.iwdael.dbroom.compiler.JavaClass.CONDITION_FLOAT_BASIC
import com.iwdael.dbroom.compiler.JavaClass.FLOAT_BASIC
import com.iwdael.dbroom.compiler.JavaClass.FLOAT_PACKING
import com.iwdael.dbroom.compiler.JavaClass.OPERATOR
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
class ConditionBasicFloatGenerator : Generator {
    override val simpleClassNameGen: String = CONDITION_FLOAT_BASIC.simpleName()
    override val packageNameGen: String = CONDITION_FLOAT_BASIC.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(
                        ParameterizedTypeName.get(
                            CONDITION,
                            TypeVariableName.get("N"),
                            TypeVariableName.get("T"),
                            FLOAT_PACKING,
                            TypeVariableName.get("Q")
                        )
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("T"), "target")
                            .addParameter(
                                ParameterizedTypeName.get(
                                    JavaClass.BASIC_COLUMN,
                                    FLOAT_PACKING
                                ), "column"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    JavaClass.CALLBACK,
                                    ParameterizedTypeName.get(
                                        CONDITION,
                                        TypeVariableName.get("N"),
                                        TypeVariableName.get("T"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("Q"),
                                    )
                                ),
                                "callBack"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    JavaClass.NEXT_BUILDER,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("Q")
                                ),
                                "builder"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    JavaClass.CREATOR,
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("Q"),
                                ),
                                "creator"
                            )
                            .addStatement("super(target, column, callBack, builder, creator)")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("equal")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = EQUAL")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("unequal")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = UNEQUAL")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("greater")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = GREATER")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("greaterEqual")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = GREATER_EQUAL")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("less")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = LESS")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("lessEqual")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = LESS_EQUAL")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("between")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value1")
                            .addParameter(FLOAT_BASIC, "value2")
                            .addStatement("this.value.add(value1)")
                            .addStatement("this.value.add(value2)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = BETWEEN")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("like")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(FLOAT_BASIC, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = BETWEEN")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("in")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ArrayTypeName.of(FLOAT_BASIC), "values")
                            .varargs()
                            .beginControlFlow("for (\$T value : values)", FLOAT_BASIC)
                            .addStatement("this.value.add(value)")
                            .endControlFlow()
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = IN")
                            .addStatement(
                                "return new \$T<N, T, \$T, Q>(this)",
                                OPERATOR,
                                FLOAT_PACKING
                            )
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    FLOAT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}