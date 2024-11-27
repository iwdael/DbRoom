package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.TypeConverter
import com.google.devtools.ksp.processing.Dependencies
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
//    private var roomCreator: MutableList<KSAnnotated> = mutableListOf()
//    private var typeConverters: MutableList<KSAnnotated> = mutableListOf()
//    private var entities: MutableList<KSAnnotated> = mutableListOf()
//    private var daos: MutableList<KSAnnotated> = mutableListOf()
//    override fun process(resolver: Resolver): List<KSAnnotated> {
//        resolver.getSymbolsWithAnnotation(DbRoomCreator::class.java.name)
//            .apply { roomCreator.addAll(this) }
//        resolver.getSymbolsWithAnnotation(TypeConverter::class.java.name)
//            .apply { typeConverters.addAll(this) }
//        resolver.getSymbolsWithAnnotation(Entity::class.java.name)
//            .apply { entities.addAll(this) }
//        resolver.getSymbolsWithAnnotation(Dao::class.java.name)
//            .apply { daos.addAll(this) }
//        process()
//        return emptyList()
//    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())
        val roomCreator = resolver.getSymbolsWithAnnotation(DbRoomCreator::class.java.name).toList()
        val typeConverters = resolver.getSymbolsWithAnnotation(TypeConverter::class.java.name).toList()
        val entities = resolver.getSymbolsWithAnnotation(Entity::class.java.name).toList()
        val daos = resolver.getSymbolsWithAnnotation(Dao::class.java.name).toList()

        if (roomCreator.size > 1) throw Exception("Annotation can only be used at most once.(${DbRoomCreator::class.java})")
        if (roomCreator.isNotEmpty()) {
            val dbRoomClassName = roomCreator.first().asKspFunction.annotation(DbRoomCreator::class)!!.value
            val className = ClassName.bestGuess(dbRoomClassName)
            MASTER_PACKAGE = className.packageName
            DB_ROOM_SIMPLE_NAME = className.simpleName
        }
        PersistenceRoomGenerator().generate(env, dependencies)
        typeConverters
            .map { it.asKspFunction }
            .apply {
                ConverterGenerator(this.toList()).generate(env, dependencies)
            }

        entities
            .map { it.asKspClass }
            .apply {
                val dao = daos.map { it.asKspClass }.toList()
                val create = roomCreator.firstOrNull()?.asKspFunction
                if (create != null) {
                    if (create.kspParameters.size != 2) throw Exception("The method is error(${create.ksp.qualifiedName?.asString()})\n$CREATOR_EXAMPLE")
                    if (create.kspParameters.first().type.qualifiedName != "android.content.Context" && create.kspParameters[1].type.qualifiedName != "java.lang.Class") throw Exception("The method is error(${create.ksp.qualifiedName?.asString()})\n$CREATOR_EXAMPLE")
                }
                DbRoomGenerator(this.toList(), dao, create).generate(env, dependencies)
            }
            .forEach {
                EntityRoomGenerator(it).generate(env, dependencies)
            }
        return emptyList()
    }

}
