package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.roomFields
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class DBGenerator(private val generator: List<Class>) : Generator {
    override fun classFull() = "com.iwdael.dbroom.DB"
    override fun simpleClassName() = "DB"
    override fun packageName() = "com.iwdael.dbroom"

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .apply {
                        generator.flatMap { it.roomFields() }
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