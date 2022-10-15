package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class DbMaker(private val generator: Generator) : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "${generator.classSimpleName}Db"

    override fun packageName() = generator.roomPackage

    override fun make(filer: Filer) {

        val column = TypeSpec.classBuilder("Column")
            .addField(String::class.java, "name", Modifier.FINAL, Modifier.PUBLIC)
            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
            .addMethod(
                MethodSpec
                    .constructorBuilder()
                    .addParameter(String::class.java, "name")
                    .addStatement("this.name = name")
                    .build()
            )
            .build()
        val staticFields = generator.clazz.fields.map {
            FieldSpec.builder(
                ClassName.get(classFull(), "Column"),
                it.name,
                Modifier.PUBLIC,
                Modifier.STATIC,
                Modifier.FINAL
            )
                .initializer("new Column(\"${it.colName()}\")")
                .build()
        }
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC)
                    .addFields(staticFields)
                    .addType(column)
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)
    }
}