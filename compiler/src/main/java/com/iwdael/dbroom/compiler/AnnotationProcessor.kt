package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import com.iwdael.dbroom.compiler.e.EClass
import com.iwdael.dbroom.compiler.maker.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class AnnotationProcessor : AbstractProcessor() {
    private var processed = false
    override fun getSupportedAnnotationTypes() = mutableSetOf(Override::class.java.canonicalName)
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annos: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        if (processed) return false
        HolderRoomMaker().make(processingEnv.filer)
        HolderConvertMaker().make(processingEnv.filer)
        HolderMaker().make(processingEnv.filer)
        (env.getElementsAnnotatedWith(Entity::class.java) ?: arrayListOf())
            .map { Generator(EClass(it)) }
            .apply {
                val dao = env.getElementsAnnotatedWith(Dao::class.java)
                    ?.map { Generator(EClass(it)) }
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