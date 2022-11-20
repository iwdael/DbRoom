package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.compiler.JavaClass.DB
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.roomFields
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class DBGenerator(private val generator: List<Class>) : Generator {
    override val simpleClassNameGen: String = DB.simpleName()
    override val packageNameGen: String = DB.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
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
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}