package com.iwdael.dblite.compiler.maker

import androidx.room.Dao
import com.iwdael.dblite.compiler.DTA
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
class RoomDbMaker(private val dta: DTA) : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "${dta.targetClassName}Db"

    override fun packageName() = Maker.ROOT_PACKAGE

    override fun make(filer: Filer) {

        val column = TypeSpec.classBuilder("Column")
            .addField(String::class.java, "name", Modifier.FINAL, Modifier.PUBLIC)
            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
            .addMethod(
                MethodSpec
                    .constructorBuilder()
                    .addParameter(String::class.java, "name")
                    .addStatement("this.name = name")
                    .build()
            )
            .build()
        val staticFields = dta.eClass.getVariable().map {
            FieldSpec.builder(ClassName.get(classFull(),"Column"), it.name() , Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL)
                .initializer("new Column(\"${it.colName()}\")")
                .build()
        }
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC)
                    .addFields(staticFields)
                    .addType(column)
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .writeTo(filer)
    }
}