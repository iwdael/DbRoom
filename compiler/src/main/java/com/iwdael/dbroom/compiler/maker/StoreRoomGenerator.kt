package com.iwdael.dbroom.compiler.maker

import androidx.room.Dao
import androidx.room.Query
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
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
class StoreRoomGenerator : Generator {
    override val simpleClassNameGen: String = "StoreRoom"
    override val packageNameGen: String = Generator.ROOT_PACKAGE
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
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
        .returns(ClassName.get(Generator.ROOT_PACKAGE, "Store"))
        .build()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen,
                TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(Dao::class.java)
                    .addMethod(store())
                    .addMethod(obtain())
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)

    }
}