package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.CONDITION
import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.JavaClass.NEXT_BUILDER
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
class NextBuilderGenerator : Generator {
    override val simpleClassNameGen: String = NEXT_BUILDER.simpleName()
    override val packageNameGen: String = NEXT_BUILDER.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.interfaceBuilder(simpleClassNameGen)
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec.methodBuilder("build")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("T"), "target")
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CALLBACK,
                                    ParameterizedTypeName.get(
                                        CONDITION,
                                        TypeVariableName.get("N"),
                                        TypeVariableName.get("T"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("Q")
                                    )
                                ),
                                "callback"
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
                                    TypeVariableName.get("Q")
                                ),
                                "creator"
                            )
                            .returns(TypeVariableName.get("N"))
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}