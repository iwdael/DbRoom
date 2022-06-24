package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.element.Method
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class HCMaker(private val generator: List<Method>) : Maker {
    override fun classFull() = "${packageName()}.${className()}"

    override fun className() = "HC"

    override fun packageName() = Maker.ROOT_PACKAGE

    override fun make(filer: Filer) {


        JavaFile
            .builder(
                packageName(),
                TypeSpec.classBuilder(className())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(
                        MethodSpec
                            .methodBuilder("ctString")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(String::class.java)
                            .addParameter(ClassName.get(Object::class.java), "value")
                            .beginControlFlow("if (value == null)")
                            .addStatement("return null")

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
                                    .filter { it.`return` == String::class.java.name }
                                    .map {
                                        val par = it.parameter.first()
                                        val type = par.type
                                        nextControlFlow(
                                            "else if (value instanceof \$T)",
                                            ClassName.bestGuess(type)
                                        )
                                        addStatement(
                                            "return ${it.owner}.${par.methodName}((\$T)value)",
                                            ClassName.bestGuess(type)
                                        )
                                    }
                            }
                            .endControlFlow()
                            .addStatement("throw new RuntimeException(\"Type not supported: \" + value.getClass().getName())")
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("ctObject")
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
                                    .filter { it.parameter.first().type == String::class.java.name }
                                    .map {
                                        nextControlFlow(
                                            "else if (clazz == \$T.class)",
                                            ClassName.bestGuess(it.`return`)
                                        )
                                        addStatement("return (T)${it.owner}.${it.name}(str)")
                                    }
                            }

                            .endControlFlow()
                            .addStatement("throw new RuntimeException(\"Type not supported: \" + clazz.getName())")
                            .build())
                    .build()
            )
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }
}