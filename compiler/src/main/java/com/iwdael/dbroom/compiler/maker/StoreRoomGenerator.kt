package com.iwdael.dbroom.compiler.maker

import androidx.room.Dao
import androidx.room.Query
import com.iwdael.dbroom.compiler.JavaClass.INDENT
import com.iwdael.dbroom.compiler.JavaClass.STORE
import com.iwdael.dbroom.compiler.JavaClass.STORE_ROOM
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
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
    override val simpleClassNameGen: String = STORE_ROOM.simpleName()
    override val packageNameGen: String = STORE_ROOM.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    private fun store() = MethodSpec.methodBuilder("store")
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .addParameter(String::class.java, "name")
        .addParameter(String::class.java, "value")
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value",
                    "\"REPLACE INTO db_store (store_name , store_value) VALUES(:name , :value)\""
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
                    "\"SELECT * FROM db_store WHERE store_name = :name LIMIT 1\""
                )
                .build()
        )
        .returns(STORE)
        .build()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen,
                TypeSpec.interfaceBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Dao::class.java)
                    .addMethod(store())
                    .addMethod(obtain())
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(INDENT)
            .build()
            .write(filer)

    }
}