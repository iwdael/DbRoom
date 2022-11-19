package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.BASIC_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.COLUMN
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
class ColumnBasicGenerator : Generator {
    override val simpleClassNameGen :String = BASIC_COLUMN.simpleName()
    override val  packageNameGen :String = BASIC_COLUMN.packageName()
    override val classNameGen:String = "${packageNameGen}.${simpleClassNameGen}"

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addTypeVariable(TypeVariableName.get("T"))
                    .superclass(ParameterizedTypeName.get(COLUMN, TypeVariableName.get("T")))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(String::class.java, "name")
                            .addStatement("super(name)")
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}