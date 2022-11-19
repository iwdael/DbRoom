package com.iwdael.dbroom.compiler.maker

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
class ColumnGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName() = COLUMN.simpleName()
    override fun packageName() = COLUMN.packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addField(
                        FieldSpec.builder(String::class.java, "name")
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build()
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(String::class.java, "name")
                            .addStatement("this.name = name")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("toString")
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("return name")
                            .returns(String::class.java)
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}