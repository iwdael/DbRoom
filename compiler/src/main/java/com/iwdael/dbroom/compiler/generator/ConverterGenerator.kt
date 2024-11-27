package com.iwdael.dbroom.compiler.generator

import com.iwdael.dbroom.compiler.compat.CONVERTER
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.kotlinsymbolprocessor.KSPFunction
import com.iwdael.kotlinsymbolprocessor.asTypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
class ConverterGenerator(private val functions: List<KSPFunction>) : KotlinGenerator {
    override val simpleClassNameGen: String = CONVERTER.simpleName
    override val packageNameGen: String = CONVERTER.packageName
    override val classNameGen: String = "${packageNameGen}.${simpleClassNameGen}"
    override fun createFileSpec(): FileSpec {
        return FileSpec
            .builder(ClassName.bestGuess(classNameGen))
            .addFileComment(FILE_COMMENT)
            .addType(
                TypeSpec
                    .objectBuilder(simpleClassNameGen)
                    .addModifiers(KModifier.PUBLIC)
                    .addFunction(
                        FunSpec
                            .builder("toString")
                            .addAnnotation(JvmStatic::class)
                            .returns(String::class.asTypeName().copy(true))
                            .addParameter("value", Any::class.asTypeName().copy(true))
                            .beginControlFlow("if (value == null)")
                            .addStatement("return null")

                            .nextControlFlow("else if (value is String)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Boolean)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Byte)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Short)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Int)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Long)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Char)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Float)")
                            .addStatement("return value.toString()")

                            .nextControlFlow("else if (value is Double)")
                            .addStatement("return value.toString()")
                            .apply {
                                functions.filter { it.kspParameters.size == 1 }
                                    .filter { it.returnType?.asTypeName()?.copy(true) == String::class.asTypeName().copy(true) }
                                    .map { function ->
                                        val kspParameter = function.kspParameters.first()
                                        val invoke = function.ksp.qualifiedName!!.asString()
                                        nextControlFlow("else if (value is %T)", kspParameter.type.asTypeName())
                                        addStatement("return ${invoke}(value)")
                                    }
                            }
                            .endControlFlow()
                            .addStatement("throw RuntimeException(\"Type not supported: \" + value.javaClass.name)")
                            .build()
                    )
                    .addFunction(
                        FunSpec
                            .builder("toObject")
                            .addAnnotation(JvmStatic::class)
                            .addTypeVariable(TypeVariableName("T : Any"))
                            .returns(TypeVariableName("T").copy(true))
                            .addParameter("str", String::class.asTypeName().copy(true))
                            .addParameter("clazz", TypeVariableName("kotlin.reflect.KClass<T>"))
                            .beginControlFlow("if (str == null)")
                            .addStatement("return null")

                            .nextControlFlow("else if (clazz == String::class)")
                            .addStatement("return str as T")

                            .nextControlFlow("else if (clazz == Boolean::class)")
                            .addStatement("return str.toBoolean() as T")

                            .nextControlFlow("else if (clazz == Byte::class)")
                            .addStatement("return str.toByte() as T")

                            .nextControlFlow("else if (clazz == Short::class)")
                            .addStatement("return str.toShort() as T")

                            .nextControlFlow("else if (clazz == Int::class)")
                            .addStatement("return str.toInt() as T")

                            .nextControlFlow("else if (clazz == Long::class)")
                            .addStatement("return str.toLong() as T")

                            .nextControlFlow("else if (clazz == Char::class)")
                            .addStatement("return str.toInt().toChar() as T")

                            .nextControlFlow("else if (clazz == Float::class)")
                            .addStatement("return str.toFloat() as T")

                            .nextControlFlow("else if (clazz == Double::class)")
                            .addStatement("return str.toDouble() as T")


                            .apply {
                                functions.filter { it.kspParameters.size == 1 }
                                    .filter { it.kspParameters.first().type.asTypeName() == String::class.asTypeName() }
                                    .map {
                                        val invoke = it.ksp.qualifiedName!!.asString()
                                        nextControlFlow(
                                            "else if (clazz == %T::class)",
                                            it.returnType!!.asTypeName().copy(false)
                                        )
                                        addStatement(
                                            "return ${invoke}(str) as T"
                                        )
                                    }
                            }

                            .endControlFlow()
                            .addStatement("throw RuntimeException(\"Type not supported: \" + clazz.qualifiedName)")

                            .build()
                    )
                    .build()
            )
            .build()
    }
}