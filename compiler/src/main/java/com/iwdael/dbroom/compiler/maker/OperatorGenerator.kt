package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.WHERE
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
class OperatorGenerator : Generator {
    override fun classFull() = "com.iwdael.dbroom.Operator"
    override fun simpleClassName() = "Operator"
    override fun packageName() = "com.iwdael.dbroom"

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("R"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(
                        FieldSpec.builder(String::class.java, "AND")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"AND\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "OR")
                            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"OR\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                WHERE,
                                TypeVariableName.get("N"),
                                TypeVariableName.get("T"),
                                TypeVariableName.get("R"),
                                TypeVariableName.get("Q"),
                            ),
                            "where"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "operator")
                            .addModifiers(Modifier.PROTECTED)
                            .initializer("null")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PROTECTED)
                            .addParameter(
                                ParameterizedTypeName.get(
                                    WHERE,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("R"),
                                    TypeVariableName.get("Q"),
                                ),
                                "where"
                            )
                            .addStatement("this.where = where")
                            .build()
                    )

                    .addMethod(
                        MethodSpec.methodBuilder("and")
                            .returns(TypeVariableName.get("N"))
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addStatement("operator = AND")
                            .addStatement("where.next = this")
                            .addStatement("return where.builder.build(where.target, where.callBack, where.builder, where.creator)")
                            .build()
                    )


                    .addMethod(
                        MethodSpec.methodBuilder("or")
                            .returns(TypeVariableName.get("N"))
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addStatement("operator = OR")
                            .addStatement("where.next = this")
                            .addStatement("return where.builder.build(where.target, where.callBack, where.builder, where.creator)")
                            .build()
                    )

                    .addMethod(
                        MethodSpec.methodBuilder("build")
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("return where.creator.create(where.target)")
                            .returns(TypeVariableName.get("Q"))
                            .build()
                    )

                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}