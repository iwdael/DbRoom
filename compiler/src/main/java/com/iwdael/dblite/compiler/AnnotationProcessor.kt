package com.iwdael.dblite.compiler

import androidx.room.Entity
import com.iwdael.dblite.compiler.e.EClass
import com.iwdael.dblite.compiler.maker.DbLiteMaker
import com.iwdael.dblite.compiler.maker.RoomMaker
import java.io.File
import java.io.FileWriter
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
        env.getElementsAnnotatedWith(Entity::class.java)!!
            .map { DTA(EClass(it)) }
            .apply {
                if (isNotEmpty()) {
                    val writer = FileWriter(createContentProvider(this[0]))
                    writer.write(DbLiteMaker(this).make())
                    writer.flush()
                    writer.close()
                }
            }
            .forEach {
                val writer = FileWriter(findGenerateFile(it))
                writer.write(RoomMaker(it).make())
                writer.flush()
                writer.close()
            }
        return false
    }

    private fun createContentProvider(element: DTA): File {
        val generateFile = File(
            processingEnv.filer.createSourceFile(
                "com.iwdael.dblite.DbLite",
                element.eClass.element
            ).toUri()
        ).apply {
            parentFile.mkdirs()
        }.let { it.parentFile }
        return File(generateFile, "DbLite.kt")
    }

    private fun findGenerateFile(DTA: DTA): File {
        val generateFile = File(
            processingEnv.filer.createSourceFile(
                DTA.generatedFullClassName,
                DTA.eClass.element
            ).toUri()
        ).apply { parentFile.mkdirs() }
        return if (DTA.eClass.sourceFileIsKotlin()) {
            File(
                generateFile.parentFile,
                "${DTA.generatedClassName}.kt"
            ).apply { parentFile.mkdirs() }
        } else generateFile
    }
}