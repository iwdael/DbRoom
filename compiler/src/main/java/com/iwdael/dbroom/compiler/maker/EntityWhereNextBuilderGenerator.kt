package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.JavaClass.WHERE
import com.iwdael.dbroom.compiler.JavaClass.WHERE_NEXT_BUILDER
import com.iwdael.dbroom.compiler.columnClassName
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.whereClassName
import com.iwdael.dbroom.compiler.whereNextBuilderClassName
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityWhereNextBuilderGenerator(private val clazz: Class) : Generator {
    override fun classFull() = "${packageName()}.${simpleClassName()}"
    override fun simpleClassName() = clazz.whereNextBuilderClassName().simpleName()
    override fun packageName() = clazz.whereNextBuilderClassName().packageName()

    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageName(), TypeSpec.classBuilder(simpleClassName())
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.FINAL)
                    .superclass(
                        ParameterizedTypeName.get(
                            WHERE_NEXT_BUILDER,
                            TypeVariableName.get("N"),
                            TypeVariableName.get("T"),
                            TypeVariableName.get("Q")
                        )
                    )
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                TypeVariableName.get("T"), "target"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CALLBACK,
                                    ParameterizedTypeName.get(
                                        WHERE,
                                        TypeVariableName.get("N"),
                                        TypeVariableName.get("T"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("Q")
                                    )
                                ), "callBack"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    JavaClass.NEXT_BUILDER,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("Q")
                                ),
                                "builder"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CREATOR,
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("Q")

                                ), "creator"
                            )
                            .addStatement("super(target, callBack, builder, creator)")
                            .build()
                    )
                    .addMethods(
                        clazz.fields
                            .distinctBy { it.asTypeName() }
                            .map {
                                MethodSpec.methodBuilder("where")
                                    .addParameter(it.columnClassName(), "column")
                                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                                    .addStatement(
                                        "return new \$T<N, T, Q>(target, column, callBack, builder, creator)",
                                        it.whereClassName()
                                    )
                                    .returns(
                                        ParameterizedTypeName.get(
                                            it.whereClassName(),
                                            TypeVariableName.get("N"),
                                            TypeVariableName.get("T"),
                                            TypeVariableName.get("Q")
                                        )
                                    )
                                    .build()
                            }
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }


}