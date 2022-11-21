package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asClassName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.BASIC_BOOLEAN_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_BYTE_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_CHAR_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_DOUBLE_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_FLOAT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_INT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_LONG_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BASIC_SHORT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.BOOLEAN_PACKING
import com.iwdael.dbroom.compiler.JavaClass.BYTE_PACKING
import com.iwdael.dbroom.compiler.JavaClass.CHAR_PACKING
import com.iwdael.dbroom.compiler.JavaClass.DOUBLE_PACKING
import com.iwdael.dbroom.compiler.JavaClass.FLOAT_PACKING
import com.iwdael.dbroom.compiler.JavaClass.INT_PACKING
import com.iwdael.dbroom.compiler.JavaClass.LONG_PACKING
import com.iwdael.dbroom.compiler.JavaClass.PACKING_BOOLEAN_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_BYTE_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_CHAR_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_DOUBLE_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_FLOAT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_INT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_LONG_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_SHORT_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.PACKING_STRING_COLUMN
import com.iwdael.dbroom.compiler.JavaClass.SHORT_PACKING
import com.iwdael.dbroom.compiler.columnClassName
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.compat.colName
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.interfaceColumnClassName
import com.iwdael.dbroom.compiler.roomFields
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class EntityColumnGenerator(private val clazz: Class) : Generator {
    override val simpleClassNameGen: String = clazz.columnClassName().simpleName()
    override val packageNameGen: String = clazz.columnClassName().packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addFields(
                        clazz.roomFields()
                            .map {
                                FieldSpec
                                    .builder(it.columnClassName(), it.name)
                                    .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)
                                    .initializer(
                                        "new \$T(\"${it.colName()}\")",
                                        it.columnClassName()
                                    )
                                    .build()
                            }
                    )
                    .addType(
                        TypeSpec.interfaceBuilder(clazz.interfaceColumnClassName())
                            .addModifiers(Modifier.PUBLIC).build()
                    )
                    .addTypes(
                        clazz.roomFields()
                            .map { it.asTypeName() }
                            .toSet()
                            .map {
                                when (it) {
                                    TypeName.BOOLEAN -> {
                                        basicType(BASIC_BOOLEAN_COLUMN, BOOLEAN_PACKING)
                                    }
                                    BOOLEAN_PACKING -> {
                                        packingType(PACKING_BOOLEAN_COLUMN, BOOLEAN_PACKING)
                                    }

                                    TypeName.BYTE -> {
                                        basicType(BASIC_BYTE_COLUMN, BYTE_PACKING)
                                    }
                                    BYTE_PACKING -> {
                                        packingType(PACKING_BYTE_COLUMN, BYTE_PACKING)
                                    }

                                    TypeName.CHAR -> {
                                        basicType(BASIC_CHAR_COLUMN, CHAR_PACKING)
                                    }
                                    CHAR_PACKING -> {
                                        packingType(PACKING_CHAR_COLUMN, CHAR_PACKING)
                                    }

                                    TypeName.DOUBLE -> {
                                        basicType(BASIC_DOUBLE_COLUMN, DOUBLE_PACKING)
                                    }
                                    DOUBLE_PACKING -> {
                                        packingType(PACKING_DOUBLE_COLUMN, DOUBLE_PACKING)
                                    }


                                    TypeName.FLOAT -> {
                                        basicType(BASIC_FLOAT_COLUMN, FLOAT_PACKING)
                                    }
                                    FLOAT_PACKING -> {
                                        packingType(PACKING_FLOAT_COLUMN, FLOAT_PACKING)
                                    }

                                    TypeName.INT -> {
                                        basicType(BASIC_INT_COLUMN, INT_PACKING)
                                    }
                                    INT_PACKING -> {
                                        packingType(PACKING_INT_COLUMN, INT_PACKING)
                                    }

                                    TypeName.LONG -> {
                                        basicType(BASIC_LONG_COLUMN, LONG_PACKING)
                                    }
                                    LONG_PACKING -> {
                                        packingType(PACKING_LONG_COLUMN, LONG_PACKING)
                                    }


                                    TypeName.SHORT -> {
                                        basicType(BASIC_SHORT_COLUMN, SHORT_PACKING)
                                    }
                                    SHORT_PACKING -> {
                                        packingType(PACKING_SHORT_COLUMN, SHORT_PACKING)
                                    }

                                    ClassName.get(String::class.java) -> {
                                        packingType(PACKING_STRING_COLUMN, it.asClassName())
                                    }
                                    else -> {
                                        packingType(PACKING_STRING_COLUMN, it.asClassName())
                                    }
                                }
                            }
                    )
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(JavaClass.INDENT)
            .build()
            .write(filer)
    }

    private fun basicType(className: ClassName, type: ClassName): TypeSpec {
        return TypeSpec.classBuilder(className.simpleName()).superclass(
            ParameterizedTypeName.get(
                JavaClass.BASIC_COLUMN,
                type
            )
        )
            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
            .addSuperinterface(clazz.interfaceColumnClassName())
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(String::class.java, "name")
                    .addStatement("super(name)")
                    .build()
            )
            .build()
    }

    private fun packingType(className: ClassName, type: ClassName): TypeSpec {
        return TypeSpec.classBuilder(className.simpleName()).superclass(
            ParameterizedTypeName.get(
                JavaClass.PACKING_COLUMN,
                type
            )
        )
            .addModifiers(Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL)
            .addSuperinterface(clazz.interfaceColumnClassName())
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(String::class.java, "name")
                    .addStatement("super(name)")
                    .build()
            )
            .build()
    }

}