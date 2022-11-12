package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.TypeConverter
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.annotations.DbRoomCreator
import com.iwdael.dbroom.compiler.maker.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class AnnotationProcessor : AbstractProcessor() {
    private var processed = false
    override fun getSupportedAnnotationTypes() = mutableSetOf(Override::class.java.canonicalName)
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annos: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        if (processed) return false
        StoreRoomGenerator().generate(processingEnv.filer)
        (env.getElementsAnnotatedWith(TypeConverter::class.java) ?: arrayListOf())
            .map { Method(it) }
            .apply {
                ConverterGenerator(this).generate(processingEnv.filer)
            }
        StoreGenerator().generate(processingEnv.filer)
        (env.getElementsAnnotatedWith(Entity::class.java) ?: arrayListOf())
            .map{ Class(it)}
            .apply {
                ObservableCreatorGenerator(this).generate(processingEnv.filer)
                DBGenerator( this).generate(processingEnv.filer)
                UseGenerator(this).generate()
            }
            .apply {
                val dao = env.getElementsAnnotatedWith(Dao::class.java)
                    ?.map { Class(it) }
                    ?: arrayListOf()
                val creates = env.getElementsAnnotatedWith(DbRoomCreator::class.java)?.toList()
                    ?: arrayListOf()
                if (creates.size > 1) throw Exception("Annotation can only be used at most once.(CreateDatabase)")
                val create = creates.firstOrNull()
                var method: Method? = null
                if (create != null) {
                    method = Method(create)
                    if (method.parameter.size != 1) throw Exception("Only one parameter can be used.(${method.parent.className}.${method.name})")
                    if (method.parameter.first().className != "android.content.Context") throw Exception(
                        "The parameter can only be Context.(${method.parent.className}.${method.name}(android.content.Context))"
                    )
                    if (method.returnClassName != "androidx.room.RoomDatabase") throw Exception("The return value of this method(${method.parent.className}.${method.name}) can only be RoomDatabase.(androidx.room.RoomDatabase)")
                }
                DbRoomGenerator(this, dao, method).generate(processingEnv.filer)
            }
            .map {
                EntityDbGenerator(it).generate(processingEnv.filer)
                it
            }
            .forEach {
                EntityRoomGenerator(it).generate(processingEnv.filer)
                EntityObservableGenerator(it).generate(processingEnv.filer)
                EntityRoomCompatGenerator(it).generate(processingEnv.filer)
            }
        processed = true
        return false
    }

}