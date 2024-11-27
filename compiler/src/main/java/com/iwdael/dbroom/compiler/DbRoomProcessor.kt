package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.iwdael.dbroom.annotations.DbRoomCreator
import com.iwdael.dbroom.compiler.compat.DB_ROOM_SIMPLE_NAME
import com.iwdael.dbroom.compiler.compat.MASTER_PACKAGE
import com.iwdael.dbroom.compiler.compat.CREATOR_EXAMPLE
import com.iwdael.dbroom.compiler.generator.ConverterGenerator
import com.iwdael.dbroom.compiler.generator.DbRoomGenerator
import com.iwdael.dbroom.compiler.generator.EntityRoomGenerator
import com.iwdael.dbroom.compiler.generator.PersistenceRoomGenerator
import com.iwdael.kotlinsymbolprocessor.asKspClass
import com.iwdael.kotlinsymbolprocessor.asKspFunction
import com.squareup.kotlinpoet.ClassName

class DbRoomProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val processed = mutableListOf<KSAnnotated>()
        val dbRoomCreators = resolver.getSymbolsWithAnnotation(DbRoomCreator::class.java.name, true).toList()
        if (dbRoomCreators.size > 1) throw Exception("Annotation can only be used at most once.(${DbRoomCreator::class.java})")
        if (dbRoomCreators.isNotEmpty()) {
            val dbRoomClassName = dbRoomCreators.first().asKspFunction.annotation(DbRoomCreator::class)!!.value
            val className = ClassName.bestGuess(dbRoomClassName)
            MASTER_PACKAGE = className.packageName
            DB_ROOM_SIMPLE_NAME = className.simpleName
        }
        processed.addAll(dbRoomCreators)
        PersistenceRoomGenerator().generate(env)
        resolver.getSymbolsWithAnnotation(TypeConverter::class.java.name, true)
            .apply { processed.addAll(this) }
            .map { it.asKspFunction }
            .apply {
                ConverterGenerator(this.toList()).generate(env)
            }

        resolver.getSymbolsWithAnnotation(Entity::class.java.name, true)
            .apply { processed.addAll(this) }
            .map { it.asKspClass }
            .apply {
                val dao = resolver.getSymbolsWithAnnotation(Dao::class.java.name, true).map { it.asKspClass }.toList()
                val create = dbRoomCreators.firstOrNull()?.asKspFunction
                if (create != null) {

                    if (create.kspParameters.size != 2) throw Exception("The method is error(${create.ksp.qualifiedName?.asString()})\n$CREATOR_EXAMPLE")
                    if (create.kspParameters.first().type.qualifiedName != "android.content.Context" && create.kspParameters[1].type.qualifiedName != "java.lang.Class") throw Exception("The method is error(${create.ksp.qualifiedName?.asString()})\n$CREATOR_EXAMPLE")
                }
                DbRoomGenerator(this.toList(), dao, create).generate(env)
            }
            .forEach {
                EntityRoomGenerator(it).generate(env)
            }
        return processed
    }

}
