package com.iwdael.dbroom.compiler.maker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class StoreGenerator : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"

    override fun simpleClassName() = "Store"

    override fun packageName() = Generator.ROOT_PACKAGE

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(simpleClassName())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(
                        AnnotationSpec.builder(Entity::class.java)
                            .addMember("tableName", "\"tb_store\"")
                            .build()
                    )
                    .addField(
                        FieldSpec.builder(String::class.java, "name")
                            .addAnnotation(PrimaryKey::class.java)
                            .addAnnotation(
                                AnnotationSpec.builder(ColumnInfo::class.java)
                                    .addMember("name", CodeBlock.of("\"store_name\""))
                                    .build()
                            )
                            .addAnnotation(NotNull::class.java)
                            .build()
                    ).addField(
                        FieldSpec.builder(String::class.java, "value")
                            .addAnnotation(
                                AnnotationSpec.builder(ColumnInfo::class.java)
                                    .addMember("name", CodeBlock.of("\"store_value\""))
                                    .build()
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec
                            .constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(String::class.java, "name")
                            .addParameter(String::class.java, "value")
                            .addStatement("this.name = name")
                            .addStatement("this.value = value")
                            .build()
                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }
}