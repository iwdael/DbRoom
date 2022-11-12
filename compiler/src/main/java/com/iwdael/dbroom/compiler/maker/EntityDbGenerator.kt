package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.roomPackage
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityDbGenerator(private val clazz: Class) : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"

    override fun simpleClassName() = "${clazz.classSimpleName}Db"

    override fun packageName() = clazz.roomPackage()

    override fun generate(filer: Filer) {

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
        val staticFields = clazz.fields.map {
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
                TypeSpec.classBuilder(simpleClassName())
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