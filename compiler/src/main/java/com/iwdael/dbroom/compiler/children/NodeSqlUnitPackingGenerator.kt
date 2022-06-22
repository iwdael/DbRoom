package com.iwdael.dbroom.compiler.children

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asClassName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.compiler.JavaClass
import com.iwdael.dbroom.compiler.convertType
import com.iwdael.dbroom.compiler.roomFields
import com.squareup.javapoet.*
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
class NodeSqlUnitPackingGenerator(private val clazz: Class, private val env: RoundEnvironment) {
    fun packingTypeSpec(): List<TypeSpec> {
        return clazz.roomFields()
            .map { it.convertType(env) }
            .filter { it != null }
            .map { it!! }
            .map {
                return@map when (it.second.returnClassName.asTypeName().box()) {
                    JavaClass.BOOLEAN_PACKING -> booleanTypeSpec(it.first, it.second)
                    JavaClass.BYTE_PACKING -> byteTypeSpec(it.first, it.second)
                    JavaClass.CHAR_PACKING -> charTypeSpec(it.first, it.second)
                    JavaClass.SHORT_PACKING -> shortTypeSpec(it.first, it.second)
                    JavaClass.INT_PACKING -> integerTypeSpec(it.first, it.second)
                    JavaClass.LONG_PACKING -> longTypeSpec(it.first, it.second)
                    JavaClass.DOUBLE_PACKING -> doubleTypeSpec(it.first, it.second)
                    JavaClass.FLOAT_PACKING -> floatTypeSpec(it.first, it.second)
                    JavaClass.STRING -> stringTypeSpec(it.first, it.second)
                    else -> throw Exception("not found TypeConverters:${it.first.parent.className}#${it.first.name}")
                }
            }
    }

    private fun typeSpecBuilder(field: Field, method: Method): TypeSpec.Builder {
        return TypeSpec.classBuilder("SqlUnitPacking${field.asTypeName().asClassName().simpleName().uppercase()}")
            .addTypeVariable(TypeVariableName.get("CONVERT"))
            .addTypeVariable(TypeVariableName.get("SOURCE"))
            .addTypeVariable(TypeVariableName.get("TARGET"))
            .addTypeVariable(TypeVariableName.get("MAPPER"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .superclass(
                ParameterizedTypeName.get(
                    JavaClass.SQL_UNIT,
                    TypeVariableName.get("CONVERT"),
                    TypeVariableName.get("SOURCE"),
                    method.returnClassName.asTypeName().box(),
                    TypeVariableName.get("TARGET"),
                    TypeVariableName.get("MAPPER"),
                )
            )
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addParameter(TypeVariableName.get("SOURCE"), "target")
                    .addParameter(ParameterizedTypeName.get(JavaClass.COLUMN, method.returnClassName.asTypeName().box()), "column")
                    // CallBack<SqlUnit<CONVERT, SOURCE, ?, TARGET, ?>> call
                    .addParameter(
                        ParameterizedTypeName.get(
                            JavaClass.CALLBACK,
                            ParameterizedTypeName.get(
                                JavaClass.SQL_UNIT,
                                TypeVariableName.get("CONVERT"),
                                TypeVariableName.get("SOURCE"),
                                TypeVariableName.get("?"),
                                TypeVariableName.get("TARGET"),
                                TypeVariableName.get("?"),
                            )
                        ), "call"
                    )
                    //SqlCreator<CONVERT, SOURCE, TARGET> builder
                    .addParameter(
                        ParameterizedTypeName.get(
                            JavaClass.SQL_CREATOR,
                            TypeVariableName.get("CONVERT"),
                            TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("TARGET")
                        ), "builder"
                    )
                    //SelectionCreator<SOURCE, TARGET> selectionCreator
                    .addParameter(
                        ParameterizedTypeName.get(
                            JavaClass.SELECTION_CREATOR,
                            TypeVariableName.get("SOURCE"),
                            TypeVariableName.get("TARGET")
                        ), "selectionCreator"
                    )
                    //SelectionCreator<SqlUnit<CONVERT, SOURCE, String, TARGET, MAPPER>, MAPPER> operatorCreator
                    .addParameter(
                        ParameterizedTypeName.get(
                            JavaClass.SELECTION_CREATOR,
                            ParameterizedTypeName.get(
                                JavaClass.SQL_UNIT,
                                TypeVariableName.get("CONVERT"),
                                TypeVariableName.get("SOURCE"),
                                method.returnClassName.asTypeName().box(),
                                TypeVariableName.get("TARGET"),
                                TypeVariableName.get("MAPPER"),
                            ),
                            TypeVariableName.get("MAPPER")
                        ), "operatorCreator"
                    )
                    .addStatement("super(target, column, call, builder, selectionCreator, operatorCreator)")
                    .build()
            )
    }

    private fun booleanTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .build()
    }

    private fun byteTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun charTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun shortTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun doubleTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun floatTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun stringTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(like(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun integerTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun longTypeSpec(field: Field, method: Method): TypeSpec {
        return typeSpecBuilder(field, method)
            .addMethod(equal(field, method))
            .addMethod(unequal(field, method))
            .addMethod(greater(field, method))
            .addMethod(greaterEqual(field, method))
            .addMethod(less(field, method))
            .addMethod(lessEqual(field, method))
            .addMethod(between(field, method))
            .addMethod(_in(field, method))
            .build()
    }

    private fun equal(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("equal")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = EQUAL")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun unequal(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("unequal")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = UNEQUAL")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun greater(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("greater")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = GREATER")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun greaterEqual(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("greaterEqual")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = GREATER_EQUAL")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun less(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("less")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = LESS")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun lessEqual(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("lessEqual")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = LESS_EQUAL")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }


    private fun between(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("between")
            .addParameter(field.asTypeName(), "value1")
            .addParameter(field.asTypeName(), "value2")
            .addStatement("this.value.add(\$T.${method.name}(value1))", method.parent.asTypeName())
            .addStatement("this.value.add(\$T.${method.name}(value2))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = BETWEEN")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun like(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("like")
            .addParameter(field.asTypeName(), "value")
            .addStatement("this.value.add(\$T.${method.name}(value))", method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = LIKE")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }

    private fun _in(field: Field, method: Method): MethodSpec {
        return MethodSpec.methodBuilder("in")
            .addParameter(ArrayTypeName.of(field.asTypeName()), "value")
            .varargs()
            .addStatement("this.value.addAll(Utils.collectionConvert(\$T.asList(value), call -> \$T.${method.name}(call)))", ClassName.get(Arrays::class.java), method.parent.asTypeName())
            .addStatement("this.call.call(this)")
            .addStatement("this.assign = IN")
            .addStatement("return operatorCreator.create(this)")
            .returns(TypeVariableName.get("MAPPER"))
            .build()
    }
}