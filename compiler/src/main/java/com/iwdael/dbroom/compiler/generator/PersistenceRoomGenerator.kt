package com.iwdael.dbroom.compiler.generator

import androidx.room.Dao
import androidx.room.Query
import com.iwdael.dbroom.compiler.compat.PERSISTENCE_ROOM
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.compat.coroutinesFunModifierCompatible
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.annotations.NotNull

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class PersistenceRoomGenerator : KotlinGenerator {
    override val simpleClassNameGen: String = PERSISTENCE_ROOM.simpleName
    override val packageNameGen: String = PERSISTENCE_ROOM.packageName
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    private fun keep() = FunSpec.builder("keep")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT, KModifier.PUBLIC)
        .addParameter("name", String::class)
        .addParameter("value", String::class.asTypeName().copy(true))
        .addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember(
                    "%L = %S",
                    "value",
                    "REPLACE INTO persistence (persistence_name , persistence_value) VALUES(:name , :value)"
                )
                .build()
        )
        .build()

    private fun acquire() = FunSpec.builder("acquire")
        .coroutinesFunModifierCompatible()
        .addModifiers(KModifier.ABSTRACT, KModifier.PUBLIC)
        .addParameter(
            ParameterSpec.builder("name", String::class)
                .addAnnotation(NotNull::class)
                .build()
        )
        .addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember(
                    "%L = %S",
                    "value",
                    "SELECT * FROM persistence WHERE persistence_name = :name LIMIT 1"
                )
                .build()
        )
        .returns(ClassName("com.iwdael.dbroom.core", "Persistence").copy(true))
        .build()

    override fun createFileSpec( ): FileSpec {
       return FileSpec.builder(ClassName.bestGuess(classNameGen))
            .addFileComment(FILE_COMMENT)
            .addType(
                TypeSpec.interfaceBuilder(simpleClassNameGen)
                    .addKdoc(TYPE_COMMENT)
                    .addAnnotation(Dao::class)
                    .addFunction(keep())
                    .addFunction(acquire())
                    .build()
            )
            .build()
    }
}