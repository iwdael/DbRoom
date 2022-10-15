package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class EntityDBMaker(private val generator: List<Generator>) : Maker {
    override fun classFull() = "${packageName()}.${className()}"
    override fun className() = "DB"
    override fun packageName() = "com.iwdael.dbroom"

    override fun make(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .apply {
                        generator.flatMap { it.roomFields }
                            .map { it.name }
                            .toHashSet()
                            .forEachIndexed { index, name ->
                                addField(
                                    FieldSpec.builder(TypeName.INT, name)
                                        .addModifiers(
                                            Modifier.FINAL,
                                            Modifier.PUBLIC,
                                            Modifier.STATIC
                                        )
                                        .initializer("$index")
                                        .build()
                                )
                            }
                    }
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }


}