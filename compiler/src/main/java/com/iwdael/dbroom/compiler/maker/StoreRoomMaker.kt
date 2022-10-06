package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class StoreRoomMaker : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "StoreRoom"

    override fun packageName() = Maker.ROOT_PACKAGE

    private fun store() = MethodSpec.methodBuilder("store")
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .addParameter(String::class.java, "name")
        .addParameter(String::class.java, "value")
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value",
                    "\"REPLACE INTO tb_store (store_name,store_value) VALUES(:name, :value)\""
                )
                .build()
        )
        .build()

    private fun obtain() = MethodSpec.methodBuilder("obtain")
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .addParameter(
            ParameterSpec.builder(String::class.java, "name")
                .addAnnotation(NotNull::class.java)
                .build()
        )
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value",
                    "\"SELECT * FROM tb_store WHERE store_name =:name LIMIT 1\""
                )
                .build()
        )
        .returns(ClassName.get(Maker.ROOT_PACKAGE, "Store"))
        .build()

    override fun make(filer: Filer) {
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(Dao::class.java)
                    .addMethod(store())
                    .addMethod(obtain())
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }
}