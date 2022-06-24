package com.iwdael.dbroom.compiler.maker

import androidx.room.*
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class StoreRoomMaker : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "StoreRoom"

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
                                ParameterSpec.builder(String::class.java, "name")
                                    .addAnnotation(NotNull::class.java)
                                    .build()
                            )
                            .addParameter(
                                ParameterSpec.builder(String::class.java, "value")
                                    .build()
                            )
                            .addAnnotation(
                                AnnotationSpec.builder(Query::class.java)
                                    .addMember(
                                        "value",
                                        "\"IF (SELECT COUNT(*) FROM tb_store WHERE store_name =:name) > 0 BEGIN UPDATE tb_store SET store_value=:value WHERE store_name=:name END ELSE BEGIN INSERT INTO tb_store VALUES(:name,:value) END\""
                                    )
                                    .build()
                            )
                            .returns(Int::class.java)
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("obtain")
                            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                            .addParameter(
                                ParameterSpec.builder(String::class.java, "name")
                                    .addAnnotation(NotNull::class.java)
                                    .build()
                            )
                            .addAnnotation(
                                AnnotationSpec.builder(Query::class.java)
                                    .addMember(
                                        "value",
                                        "\"SELECT * FROM tb_store WHERE store_name =:name LIMIT 1\""
                                    )
                                    .build()
                            )
                            .returns(ClassName.get(Maker.ROOT_PACKAGE, "Store"))
                            .build()
                    )
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }
}