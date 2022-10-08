package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.TypeConverter
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.annotation.DbRoomCreator
import com.iwdael.dbroom.compiler.maker.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class AnnotationProcessor : AbstractProcessor() {
    private var processed = false
    override fun getSupportedAnnotationTypes() = mutableSetOf(Override::class.java.canonicalName)
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annos: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        if (processed) return false
        StoreRoomMaker().make(processingEnv.filer)
        (env.getElementsAnnotatedWith(TypeConverter::class.java) ?: arrayListOf())
            .map { Method(it) }
            .apply {
                ConverterMaker(this).make(processingEnv.filer)
            }
        StoreMaker().make(processingEnv.filer)
        (env.getElementsAnnotatedWith(Entity::class.java) ?: arrayListOf())
            .apply {
                RoomObserverMaker(this.toMutableList().map { Generator(Class(it)) }).make(
                    processingEnv.filer
                )
                ObserverMaker().make(processingEnv.filer)
                EntityDBMaker(
                    this.toMutableList().map { Generator(Class(it)) }).make(processingEnv.filer)
                RoomMapHandler(this.toMutableList().map { Generator(Class(it)) }).handle()
            }
            .map { Generator(Class(it)) }
            .apply {
                val dao = env.getElementsAnnotatedWith(Dao::class.java)
                    ?.map { Generator(Class(it)) }
                    ?: arrayListOf()
                val creates = env.getElementsAnnotatedWith(DbRoomCreator::class.java)?.toList()
                    ?: arrayListOf()
                if (creates.size > 1) throw Exception("Annotation can only be used at most once.(CreateDatabase)")
                val create = creates.firstOrNull()
                var method: Method? = null
                if (create != null) {
                    method = Method(create)
                    if (method.parameter.size != 1) throw Exception("Only one parameter can be used.(${method.owner}.${method.name})")
                    if (method.parameter.first().type != "android.content.Context") throw Exception(
                        "The parameter can only be Context.(${method.owner}.${method.name}(android.content.Context))"
                    )
                    if (method.`return` != "androidx.room.RoomDatabase") throw Exception("The return value of this method(${method.owner}.${method.name}) can only be RoomDatabase.(androidx.room.RoomDatabase)")
                }
                DbRoomMaker(this, dao, method).make(processingEnv.filer)
            }
            .map {
                DbMaker(it).make(processingEnv.filer)
                it
            }
            .forEach {
                RoomMaker(it).make(processingEnv.filer)
                EntityObserverMaker(it).make(processingEnv.filer)
                RoomCompatMaker(it).make(processingEnv.filer)
            }
        processed = true
        return false
    }

}