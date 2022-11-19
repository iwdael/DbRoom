package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class CreatorGenerator : Generator {
    override val simpleClassNameGen: String = CREATOR.simpleName()
    override val packageNameGen: String = CREATOR.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.interfaceBuilder(simpleClassNameGen)
                    .addTypeVariable(TypeVariableName.get("C"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec.methodBuilder("create")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("C"), "call")
                            .returns(TypeVariableName.get("T"))
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}