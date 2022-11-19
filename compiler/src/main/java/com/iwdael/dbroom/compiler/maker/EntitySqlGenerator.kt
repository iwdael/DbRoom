package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.dbroom.compiler.*
import com.iwdael.dbroom.compiler.JavaClass.CONDITION
import com.iwdael.dbroom.compiler.JavaClass.UTILS
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntitySqlGenerator(private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = clazz.sqlClassName().simpleName()
    override val packageNameGen: String = clazz.sqlClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addType(
                        TypeSpec.classBuilder(clazz.sqlQueryClassName())
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .addField(
                                FieldSpec.builder(String::class.java, "selection")
                                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                                    .build()
                            )
                            .addField(
                                FieldSpec.builder(ArrayTypeName.of(Object::class.java), "bindArgs")
                                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.constructorBuilder()
                                    .addModifiers(Modifier.PROTECTED)
                                    .addParameter(String::class.java, "selection")
                                    .addParameter(ArrayTypeName.of(Object::class.java), "bindArgs")
                                    .addStatement("this.selection = selection")
                                    .addStatement("this.bindArgs = bindArgs")
                                    .build()
                            )
                            .build()
                    )

                    .addType(
                        TypeSpec.classBuilder("QueryBuilder")
                            .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                            .addField(
                                FieldSpec.builder(
                                    ArrayTypeName.of(clazz.interfaceColumnClassName()),
                                    "fields"
                                )
                                    .addModifiers(Modifier.PRIVATE)
                                    .build()
                            )
                            .addField(
                                FieldSpec.builder(
                                    ParameterizedTypeName.get(
                                        ClassName.get(List::class.java),
                                        ParameterizedTypeName.get(
                                            CONDITION,
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?"),
                                            TypeVariableName.get("?")
                                        )
                                    ),
                                    "wheres"
                                )
                                    .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                                    .initializer("new \$T()", ArrayList::class.java)
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("fields")
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addParameter(
                                        ArrayTypeName.of(clazz.interfaceColumnClassName()),
                                        "fields"
                                    )
                                    .addStatement("this.fields = fields")
                                    .varargs()
                                    .returns(
                                        ParameterizedTypeName.get(
                                            clazz.whereBuilderClassName(),
                                            ParameterizedTypeName.get(
                                                clazz.whereBuilder2ClassName(),
                                                TypeVariableName.get("?"),
                                                clazz.sqlQueryBuilderClassName(),
                                                clazz.sqlQueryClassName()
                                            ),
                                            clazz.sqlQueryBuilderClassName(),
                                            clazz.sqlQueryClassName()
                                        )
                                    )
                                    .addStatement(
                                        "return new \$T<>(this, wheres::add, \$T::new, builder -> new \$T(builder.toSelection(), builder.toBindArgs()))",
                                        clazz.whereBuilderClassName(),
                                        clazz.whereBuilder2ClassName(),
                                        clazz.sqlQueryClassName()
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toSelection")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(String::class.java)
                                    .addStatement(
                                        "return \$T.toSelection(\"${clazz.roomTableName()}\", fields, wheres)",
                                        UTILS
                                    )
                                    .build()
                            )
                            .addMethod(
                                MethodSpec.methodBuilder("toBindArgs")
                                    .addModifiers(Modifier.PRIVATE)
                                    .returns(ArrayTypeName.of(Object::class.java))
                                    .addStatement("return \$T.toBindArgs(wheres)", UTILS)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }
}