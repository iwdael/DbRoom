package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.ROOM_OBSERVABLE_CREATOR
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.bestGuessClassName
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.observerClassName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
class ObservableCreatorGenerator(private val classes: List<Class>) : Generator {
    override fun classFull() =
        "${ROOM_OBSERVABLE_CREATOR.packageName()}.${ROOM_OBSERVABLE_CREATOR.simpleName()}"

    override fun simpleClassName() = "RoomObservableCreator"

    override fun packageName() = "com.iwdael.dbroom"

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addSuperinterface("${packageName()}.core.ObservableCreator".bestGuessClassName())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec.methodBuilder("create")
                            .addParameter(ClassName.get(Object::class.java), "obj")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(JavaClass.BASE_OBSERVABLE)
                            .addStatement("Class<?> clazz = obj.getClass()")
                            .apply {
                                if (classes.isEmpty()) return@apply
                                classes.forEachIndexed { index, clazz ->
                                    if (index == 0) {
                                        beginControlFlow(
                                            "if(clazz == \$T.class)",
                                            clazz.asTypeName()
                                        )
                                        addStatement(
                                            "return new \$T((\$T) obj)",
                                            clazz.observerClassName().asTypeName(),
                                            clazz.asTypeName()
                                        )
                                    } else {
                                        nextControlFlow(
                                            "else if(clazz == \$T.class)",
                                            clazz.asTypeName()
                                        )
                                        addStatement(
                                            "return new \$T((\$T) obj)",
                                            clazz.observerClassName().asTypeName(),
                                            clazz.asTypeName()
                                        )
                                    }
                                }
                                nextControlFlow("else")
                                addStatement("return null")
                                endControlFlow()
                            }
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }
}