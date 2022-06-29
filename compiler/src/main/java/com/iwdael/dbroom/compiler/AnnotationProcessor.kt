package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.TypeConverter
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.Class
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

            .map { Generator(Class(it)) }
            .apply {
                val dao = env.getElementsAnnotatedWith(Dao::class.java)
                    ?.map { Generator(Class(it)) }
                    ?: arrayListOf()
                DbRoomMaker(this, dao).make(processingEnv.filer)
            }
            .map {
                DbMaker(it).make(processingEnv.filer)
                it
            }
            .forEach {
                RoomMaker(it).make(processingEnv.filer)
            }
        processed = true
        return false
    }

}