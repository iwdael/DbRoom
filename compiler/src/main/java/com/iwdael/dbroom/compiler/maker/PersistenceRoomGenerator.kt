package com.iwdael.dbroom.compiler.maker

import androidx.room.Dao
import androidx.room.Query
import com.iwdael.dbroom.compiler.JavaClass.INDENT
import com.iwdael.dbroom.compiler.JavaClass.PERSISTENCE
import com.iwdael.dbroom.compiler.JavaClass.PERSISTENCE_ROOM
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
class PersistenceRoomGenerator : Generator {
    override val simpleClassNameGen: String = PERSISTENCE_ROOM.simpleName()
    override val packageNameGen: String = PERSISTENCE_ROOM.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    private fun keep() = MethodSpec.methodBuilder("keep")
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .addParameter(String::class.java, "name")
        .addParameter(String::class.java, "value")
        .addAnnotation(
            AnnotationSpec.builder(Query::class.java)
                .addMember(
                    "value",
                    "\"REPLACE INTO persistence (persistence_name , persistence_value) VALUES(:name , :value)\""
                )
                .build()
        )
        .build()

    private fun acquire() = MethodSpec.methodBuilder("acquire")
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
                    "\"SELECT * FROM persistence WHERE persistence_name = :name LIMIT 1\""
                )
                .build()
        )
        .returns(PERSISTENCE)
        .build()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen,
                TypeSpec.interfaceBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Dao::class.java)
                    .addMethod(keep())
                    .addMethod(acquire())
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(INDENT)
            .build()
            .write(filer)

    }
}