package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.CONDITION
import com.iwdael.dbroom.compiler.JavaClass.UTILS
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.bestGuessClassName
import com.iwdael.dbroom.compiler.compat.charLower
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class UtilsGenerator : Generator {
    override val simpleClassNameGen: String = UTILS.simpleName()
    override val packageNameGen: String = UTILS.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(
                        FieldSpec.builder(String::class.java, "SELECT")
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED)
                            .initializer("\"SELECT\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "WHERE")
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED)
                            .initializer("\"WHERE\"")
                            .build()
                    ).addField(
                        FieldSpec.builder(String::class.java, "FROM")
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED)
                            .initializer("\"FROM\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "SPACE")
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED)
                            .initializer("\" \"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "AND")
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PROTECTED)
                            .initializer("\"AND\"")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("toSelection")
                            .addParameter(String::class.java, "tableName")
                            .addParameter(ArrayTypeName.of(Object::class.java), "columns")
                            .addParameter(
                                ParameterizedTypeName.get(
                                    ClassName.get(List::class.java),
                                    ParameterizedTypeName.get(
                                        CONDITION,
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("?")
                                    )
                                ), "wheres"
                            )
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(String::class.java)
                            .addStatement(
                                "\$T builder = new \$T()",
                                StringBuilder::class.java,
                                StringBuilder::class.java
                            )
                            .addStatement("builder.append(SELECT).append(SPACE)")
                            .beginControlFlow("for (Object column : columns)")
                            .addStatement("builder.append(column).append(SPACE)")
                            .endControlFlow()
                            .beginControlFlow("if (columns.length == 0)")
                            .addStatement("builder.append(\"*\").append(SPACE)")
                            .endControlFlow()
                            .addStatement("builder.append(FROM).append(SPACE).append(tableName).append(SPACE)")
                            .beginControlFlow("if (wheres.isEmpty())")
                            .addStatement("return builder.toString()")
                            .nextControlFlow("else")
                            .addStatement("builder.append(toWhere(wheres))")
                            .addStatement("return builder.toString()")
                            .endControlFlow()
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("toBindArgs")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addParameter(
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
                            .returns(ArrayTypeName.of(Object::class.java))
                            .addStatement(
                                "\$T<\$T> bindArgs = new \$T<>()",
                                List::class.java,
                                Object::class.java,
                                ArrayList::class.java
                            )
                            .beginControlFlow(
                                "for (\$T<?, ?, ?, ?> where : wheres)",
                                CONDITION
                            )
                            .addStatement("bindArgs.addAll(where.value)")
                            .endControlFlow()
                            .addStatement("return bindArgs.toArray()")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("toWhere")
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                            .returns(String::class.java)
                            .addParameter(
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
                                "${CONDITION.simpleName().charLower()}s"
                            )
                            .addStatement(
                                "\$T builder = new \$T()",
                                StringBuilder::class.java,
                                StringBuilder::class.java
                            )
                            .beginControlFlow(
                                "if(!${
                                    CONDITION.simpleName().charLower()
                                }s.isEmpty())"
                            )
                            .addStatement("builder.append(WHERE).append(SPACE)")
                            .endControlFlow()

                            .beginControlFlow(
                                "for (\$T<?, ?, ?, ?> ${
                                    CONDITION.simpleName().charLower()
                                } : ${CONDITION.simpleName().charLower()}s)",
                                CONDITION
                            )

                            .beginControlFlow(
                                "if (\$T.equals(${
                                    CONDITION.simpleName().charLower()
                                }.assign, \$T.BETWEEN))",
                                "java.util.Objects".bestGuessClassName(),
                                CONDITION
                            )
                            .addStatement(
                                "builder.append(${
                                    CONDITION.simpleName().charLower()
                                }.column).append(SPACE)" +
                                        ".append(${
                                            CONDITION.simpleName().charLower()
                                        }.assign).append(SPACE)" +
                                        ".append('?').append(SPACE)" +
                                        ".append(AND).append(SPACE)" +
                                        ".append('?').append(SPACE)"
                            )
                            .nextControlFlow(
                                "else if (Objects.equals(${
                                    CONDITION.simpleName().charLower()
                                }.assign, \$T.IN))",
                                CONDITION
                            )
                            .addStatement(
                                "builder.append(${
                                    CONDITION.simpleName().charLower()
                                }.column).append(SPACE)" +
                                        ".append(${
                                            CONDITION.simpleName().charLower()
                                        }.assign).append(SPACE)" +
                                        ".append('(')"
                            )
                            .addStatement(
                                "int count = ${
                                    CONDITION.simpleName().charLower()
                                }.value.size()"
                            )
                            .beginControlFlow("for (int index = 0; index < count; index++)")
                            .addStatement("builder.append(\"?\")")
                            .beginControlFlow("if (index != count - 1)")
                            .addStatement("builder.append(SPACE).append(\",\").append(SPACE)")
                            .endControlFlow()
                            .endControlFlow()
                            .addStatement("builder.append(')').append(SPACE)")
                            .nextControlFlow("else")
                            .addStatement(
                                "builder.append(${
                                    CONDITION.simpleName().charLower()
                                }.column).append(SPACE)" +
                                        ".append(${
                                            CONDITION.simpleName().charLower()
                                        }.assign).append(SPACE)" +
                                        ".append('?').append(SPACE)"
                            )
                            .endControlFlow()
                            .beginControlFlow(
                                "if (${
                                    CONDITION.simpleName().charLower()
                                }.next != null)"
                            )
                            .addStatement(
                                "builder.append(${
                                    CONDITION.simpleName().charLower()
                                }.next.operator).append(SPACE)"
                            )
                            .endControlFlow()
                            .endControlFlow()

                            .addStatement("return builder.toString()")
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}