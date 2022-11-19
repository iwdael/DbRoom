package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.COLUMN
import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.JavaClass.NEXT_BUILDER
import com.iwdael.dbroom.compiler.JavaClass.OPERATOR
import com.iwdael.dbroom.compiler.JavaClass.CONDITION
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
class WhereGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName(): String = CONDITION.simpleName()
    override fun packageName(): String = CONDITION.packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("R"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addField(
                        FieldSpec.builder(String::class.java, "EQUAL")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"=\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "UNEQUAL")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"<>\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "GREATER")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\">\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "GREATER_EQUAL")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\">=\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "LESS")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"<\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "LESS_EQUAL")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"<=\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "BETWEEN")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"BETWEEN\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "LIKE")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"LIKE\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "IN")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"IN\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(TypeVariableName.get("T"), "target")
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                CALLBACK,
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
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                CREATOR,
                                TypeVariableName.get("T"),
                                TypeVariableName.get("Q"),
                            ),
                            "creator"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                COLUMN,
                                TypeVariableName.get("R")
                            ), "column"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                ClassName.get(List::class.java),
                                TypeVariableName.get("R")
                            ), "value"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .initializer("new \$T<>()", ClassName.get(ArrayList::class.java))
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                NEXT_BUILDER,
                                TypeVariableName.get("N"),
                                TypeVariableName.get("T"),
                                TypeVariableName.get("Q")
                            ),
                            "builder"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "assign")
                            .addModifiers(Modifier.PROTECTED)
                            .initializer("null")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                OPERATOR,
                                TypeVariableName.get("N"),
                                TypeVariableName.get("T"),
                                TypeVariableName.get("R"),
                                TypeVariableName.get("Q"),
                            ),
                            "next"
                        )
                            .addModifiers(Modifier.PROTECTED)
                            .initializer("null")
                            .build()
                    )

                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("T"), "target")
                            .addParameter(
                                ParameterizedTypeName.get(
                                    COLUMN,
                                    TypeVariableName.get("R")
                                ), "column"
                            )

                            .addParameter(
                                ParameterizedTypeName.get(
                                    CALLBACK,
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
                                    NEXT_BUILDER,
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
                            .addStatement("this.column = column")
                            .addStatement("this.target = target")
                            .addStatement("this.callBack = callBack")
                            .addStatement("this.builder = builder")
                            .addStatement("this.creator = creator")
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}