package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.FLOAT_PACKING
import com.iwdael.dbroom.compiler.JavaClass.PACKING_FLOAT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_COLUMN
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class ColumnPackingFloatGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName() = PACKING_FLOAT_COLUMN.simpleName()
    override fun packageName() = PACKING_FLOAT_COLUMN.packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .superclass(ParameterizedTypeName.get(PACKING_COLUMN, FLOAT_PACKING))
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