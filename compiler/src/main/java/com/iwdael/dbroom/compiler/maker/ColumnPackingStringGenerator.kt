package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.PACKING_STRING_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_COLUMN
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
class ColumnPackingStringGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName() = PACKING_STRING_COLUMN.simpleName()
    override fun packageName() = PACKING_STRING_COLUMN.packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .superclass(ParameterizedTypeName.get(PACKING_COLUMN, ClassName.get(String::class.java)))
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