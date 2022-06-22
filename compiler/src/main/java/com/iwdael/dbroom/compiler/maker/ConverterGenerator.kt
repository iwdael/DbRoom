package com.iwdael.dbroom.compiler.maker

import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.JavaClass.CONVERTER
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.TYPE_COMMENT
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class ConverterGenerator(private val generator: List<Method>) : Generator {
    override val simpleClassNameGen: String = CONVERTER.simpleName()
    override val packageNameGen: String = CONVERTER.packageName()
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen,
                TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec
                            .methodBuilder("toString")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(String::class.java)
                            .addParameter(ClassName.get(Object::class.java), "value")
                            .beginControlFlow("if (value == null)")
                            .addStatement("return null")

                            .nextControlFlow("else if (value instanceof String)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Boolean)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Byte)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Short)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Integer)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Long)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Character)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Float)")
                            .addStatement("return String.valueOf(value)")

                            .nextControlFlow("else if (value instanceof Double)")
                            .addStatement("return String.valueOf(value)")
                            .apply {
                                generator.filter { it.parameter.size == 1 }
                                    .filter { it.returnClassName == String::class.java.name }
                                    .map {
                                        val par = it.parameter.first()
                                        nextControlFlow(
                                            "else if (value instanceof \$T)",
                                            par.asTypeName()
                                        )
                                        addStatement(
                                            "return ${it.parent.className}.${it.name}((\$T)value)",
                                            par.asTypeName()
                                        )
                                    }
                            }
                            .endControlFlow()
                            .addStatement("throw new RuntimeException(\"Type not supported: \" + value.getClass().getName())")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("toObject")
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addTypeVariable(TypeVariableName.get("T"))
                            .returns(Object::class.java)
                            .addParameter(
                                ParameterSpec.builder(ClassName.get(String::class.java), "str")
                                    .build()
                            )
                            .addParameter(
                                ParameterSpec
                                    .builder(TypeVariableName.get("Class<T>"), "clazz")
                                    .build()
                            )
                            .beginControlFlow("if (str == null)")
                            .addStatement("return null")

                            .nextControlFlow("else if (clazz == String.class)")
                            .addStatement("return str")

                            .nextControlFlow("else if (clazz == Boolean.class)")
                            .addStatement("return Boolean.parseBoolean(str)")

                            .nextControlFlow("else if (clazz == Byte.class)")
                            .addStatement("return Byte.parseByte(str)")

                            .nextControlFlow("else if (clazz == Short.class)")
                            .addStatement("return Short.parseShort(str)")

                            .nextControlFlow("else if (clazz == Integer.class)")
                            .addStatement("return Integer.parseInt(str)")

                            .nextControlFlow("else if (clazz == Long.class)")
                            .addStatement("return Long.parseLong(str)")

                            .nextControlFlow("else if (clazz == Character.class)")
                            .addStatement("return new Character(str.charAt(0))")

                            .nextControlFlow("else if (clazz == Float.class)")
                            .addStatement("return Float.parseFloat(str)")

                            .nextControlFlow("else if (clazz == Double.class)")
                            .addStatement("return Double.parseDouble(str)")

                            .nextControlFlow("else if (clazz == Double.class)")
                            .addStatement("return Double.parseDouble(str)")

                            .apply {
                                generator.filter { it.parameter.size == 1 }
                                    .filter { it.parameter.first().className == String::class.java.name }
                                    .map {
                                        nextControlFlow(
                                            "else if (clazz == \$T.class)",
                                            it.returnClassName.asTypeName()
                                        )
                                        addStatement(
                                            "return ${it.parent.className}.${it.name}(str)"
                                        )
                                    }
                            }

                            .endControlFlow()
                            .addStatement("throw new RuntimeException(\"Type not supported: \" + clazz.getName())")
                            .build())
                    .addJavadoc(TYPE_COMMENT)
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .indent(JavaClass.INDENT)
            .build()
            .write(filer)

    }
}