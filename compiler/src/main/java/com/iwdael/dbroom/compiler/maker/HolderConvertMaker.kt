package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class HolderConvertMaker : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "HolderConverter"

    override fun packageName() = Maker.ROOT_PACKAGE

    override fun make(filer: Filer) {
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec
                            .methodBuilder("converterString")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(String::class.java)
                            .addParameter(ClassName.get(Object::class.java) , "value")
                            .addStatement("throw new RuntimeException(\"Type not supported: \" + value.getClass().getName())")
                            .build()
                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }
}