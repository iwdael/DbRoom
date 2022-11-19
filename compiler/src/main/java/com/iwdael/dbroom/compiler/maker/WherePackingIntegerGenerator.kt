package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.PACKING_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.JavaClass.INT_PACKING
import com.iwdael.dbroom.compiler.JavaClass.OPERATOR
import com.iwdael.dbroom.compiler.JavaClass.WHERE
import com.iwdael.dbroom.compiler.JavaClass.WHERE_INTEGER_PACKING
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class WherePackingIntegerGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName(): String = WHERE_INTEGER_PACKING.simpleName()
    override fun packageName(): String = WHERE_INTEGER_PACKING.packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(
                        ParameterizedTypeName.get(
                            WHERE,
                            TypeVariableName.get("N"),
                            TypeVariableName.get("T"),
                            INT_PACKING,
                            TypeVariableName.get("Q")
                        )
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("T"), "target")
                            .addParameter(
                                ParameterizedTypeName.get(
                                    PACKING_COLUMN,
                                    INT_PACKING
                                ), "column"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CALLBACK,
                                    ParameterizedTypeName.get(
                                        WHERE,
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
                                    CREATOR,
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
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = EQUAL")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("unequal")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = UNEQUAL")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("greater")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = GREATER")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("greaterEqual")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = GREATER_EQUAL")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("less")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = LESS")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("lessEqual")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = LESS_EQUAL")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("between")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value1")
                            .addParameter(INT_PACKING, "value2")
                            .addStatement("this.value.add(value1)")
                            .addStatement("this.value.add(value2)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = BETWEEN")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("like")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(INT_PACKING, "value")
                            .addStatement("this.value.add(value)")
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = BETWEEN")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
                                    TypeVariableName.get("Q"),
                                )
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("in")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ArrayTypeName.of(INT_PACKING), "value")
                            .varargs()
                            .addStatement(
                                "this.value.addAll(\$T.asList(value))",
                                Arrays::class.java
                            )
                            .addStatement("this.callBack.call(this)")
                            .addStatement("this.assign = IN")
                            .addStatement("return new \$T<N, T, \$T, Q>(this)", OPERATOR, INT_PACKING)
                            .returns(
                                ParameterizedTypeName.get(
                                    OPERATOR,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    INT_PACKING,
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