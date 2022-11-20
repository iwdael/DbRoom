package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass.CALLBACK
import com.iwdael.dbroom.compiler.JavaClass.CONDITION
import com.iwdael.dbroom.compiler.JavaClass.CONDITION_BUILDER
import com.iwdael.dbroom.compiler.JavaClass.CREATOR
import com.iwdael.dbroom.compiler.JavaClass.NEXT_BUILDER
import com.iwdael.dbroom.compiler.columnClassName
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.whereBuilderClassName
import com.iwdael.dbroom.compiler.whereClassName
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityConditionBuilderGenerator(private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = clazz.whereBuilderClassName().simpleName()
    override val packageNameGen: String = clazz.whereBuilderClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {


        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addTypeVariable(TypeVariableName.get("N"))
                    .addTypeVariable(TypeVariableName.get("T"))
                    .addTypeVariable(TypeVariableName.get("Q"))
                    .superclass(
                        ParameterizedTypeName.get(
                            CONDITION_BUILDER,
                            TypeVariableName.get("N"),
                            TypeVariableName.get("T"),
                            TypeVariableName.get("Q")
                        )
                    )
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.FINAL)
                    .addMethod(
                        MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeVariableName.get("T"), "target")
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CALLBACK,
                                    ParameterizedTypeName.get(
                                        CONDITION,
                                        TypeVariableName.get("N"),
                                        TypeVariableName.get("T"),
                                        TypeVariableName.get("?"),
                                        TypeVariableName.get("Q")
                                    )
                                ),
                                "call"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    NEXT_BUILDER,
                                    TypeVariableName.get("N"),
                                    TypeVariableName.get("T"),
                                    TypeVariableName.get("Q")
                                ),
                                "builder"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    CREATOR, TypeVariableName.get("T"),
                                    TypeVariableName.get("Q")
                                ),
                                "creator"
                            )
                            .addStatement("super(target, call, builder, creator)")
                            .build()
                    )
                    .addMethods(
                        clazz.roomFields()
                            .distinctBy { it.asTypeName() }
                            .map {
                                MethodSpec.methodBuilder("where")
                                    .addParameter(it.columnClassName(), "column")
                                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                                    .addStatement(
                                        "return new \$T<N, T, Q>(target, column, call, builder, creator)",
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