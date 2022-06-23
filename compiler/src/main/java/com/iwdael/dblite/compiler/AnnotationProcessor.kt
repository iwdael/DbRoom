package com.iwdael.dblite.compiler

import androidx.room.Entity
import com.iwdael.dblite.compiler.e.EClass
import com.iwdael.dblite.compiler.maker.DbLiteMaker
import com.iwdael.dblite.compiler.maker.RoomDbMaker
import com.iwdael.dblite.compiler.maker.RoomMaker
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class AnnotationProcessor : AbstractProcessor() {
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Entity::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annos: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        val entities = env.getElementsAnnotatedWith(Entity::class.java) ?: return false
        if (entities.isEmpty()) return false
        entities.map { DTA(EClass(it)) }
            .apply { DbLiteMaker(this).make(processingEnv.filer) }
            .forEach {
                RoomDbMaker(it).make(processingEnv.filer)
                RoomMaker(it).make(processingEnv.filer)
            }

        return false
    }
}