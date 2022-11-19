package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.JavaClass.WHERE
import com.iwdael.dbroom.compiler.JavaClass.CONDITION_BUILDER_2
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
class Condition2BuilderGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName(): String = CONDITION_BUILDER_2.simpleName()
    override fun packageName() = CONDITION_BUILDER_2.packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.ABSTRACT)
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
                                    WHERE,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("?"),
                                    TypeVariableName.get("Q")
                                )
                            ), "callBack"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )

                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                CREATOR,
                                TypeVariableName.get("T"),
                                TypeVariableName.get("Q")

                            ), "creator"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                JavaClass.NEXT_BUILDER,
                                TypeVariableName.get("N"),
                                TypeVariableName.get("T"),
                                TypeVariableName.get("Q")
                            ),
                            "builder"
                        )
                            .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                            .build()
                    )

                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                TypeVariableName.get("T"), "target"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CALLBACK,
                                    ParameterizedTypeName.get(
                                        WHERE,
                                        TypeVariableName.get("N"),
                                        TypeVariableName.get("T"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("Q")
                                    )
                                ), "callBack"
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
                                    TypeVariableName.get("Q")

                                ), "creator"
                            )
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