package com.iwdael.dbroom.compiler

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.TypeConverter
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.dbroom.annotations.DbRoomCreator
import com.iwdael.dbroom.compiler.JavaClass.MASTER_PACKAGE
import com.iwdael.dbroom.compiler.compat.CREATOR_EXAMPLE
import com.iwdael.dbroom.compiler.compat.packageName
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
        MASTER_PACKAGE = env.packageName()
        StoreRoomGenerator().generate(processingEnv.filer)
        (env.getElementsAnnotatedWith(TypeConverter::class.java) ?: arrayListOf()).map { Method(it) }.apply {
            ConverterGenerator(this).generate(processingEnv.filer)
        }
        (env.getElementsAnnotatedWith(Entity::class.java) ?: arrayListOf()).map { Class(it) }.apply {
            DBGenerator(this).generate(processingEnv.filer)
            UseGenerator(this).generate()
        }.apply {
            val dao = env.getElementsAnnotatedWith(Dao::class.java)?.map { Class(it) } ?: arrayListOf()
            val creates = env.getElementsAnnotatedWith(DbRoomCreator::class.java)?.toList() ?: arrayListOf()
            if (creates.size > 1) throw Exception("Annotation can only be used at most once.(CreateDatabase)")
            val create = creates.firstOrNull()
            var method: Method? = null
            if (create != null) {
                method = Method(create)
                if (method.parameter.size != 2) throw Exception("The method is error(${method.parent.className}.${method.name})\n${CREATOR_EXAMPLE}")
                if (method.parameter.first().className != "android.content.Context" && method.parameter[1].className != "java.lang.Class" ) throw Exception("The method is error(${method.parent.className}.${method.name})\n${CREATOR_EXAMPLE}")
            }
            DbRoomGenerator(this, dao, method).generate(processingEnv.filer)
        }.map {
            EntityColumnGenerator(it).generate(processingEnv.filer)
            EntityConditionBuilderGenerator(it).generate(processingEnv.filer)
            EntityCondition2BuilderGenerator(it).generate(processingEnv.filer)
            EntitySqlGenerator(it).generate(processingEnv.filer)
            it
        }.forEach {
            EntityRoomGenerator(it).generate(processingEnv.filer)
            EntityNotifierGenerator(it).generate(processingEnv.filer)
        }
        processed = true
        return false
    }

}