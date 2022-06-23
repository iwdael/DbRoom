package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class HolderRoomMaker : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "HolderRoom"

    override fun packageName() = Maker.ROOT_PACKAGE

    override fun make(filer: Filer) {
        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(Dao::class.java)
                    .addMethod(
                        MethodSpec.methodBuilder("store")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(
                                ParameterSpec.builder(ClassName.get(Maker.ROOT_PACKAGE,"Holder"), "entity")
                                    .addAnnotation(NotNull::class.java)
                                    .build()
                            )
                            .addAnnotation(
                                AnnotationSpec.builder(Insert::class.java)
                                    .addMember("entity", "Holder.class")
                                    .build()
                            )
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("obtain")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(String::class.java, "name")
                            .addAnnotation(
                                AnnotationSpec.builder(Query::class.java)
                                    .addMember(
                                        "value",
                                        "\"SELECT * FROM tb_holder WHERE holder_name =:name LIMIT 1\""
                                    )
                                    .build()
                            )
                            .returns(ClassName.get(Maker.ROOT_PACKAGE, "Holder"))
                            .build()
                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }
}