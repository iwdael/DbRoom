package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
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
class CallBackGenerator : Generator {
    override val simpleClassNameGen: String = CALLBACK.simpleName()
    override val packageNameGen: String = CALLBACK.packageName()
    override val classNameGen = "${packageNameGen}.${simpleClassNameGen}"


    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.interfaceBuilder(simpleClassNameGen)
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec.methodBuilder("call")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("T"), "call")
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}