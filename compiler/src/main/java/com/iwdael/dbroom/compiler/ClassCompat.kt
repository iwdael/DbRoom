package com.iwdael.dbroom.compiler

import androidx.room.*
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Field
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asClassName
import com.iwdael.annotationprocessorparser.poet.JavaPoet.asTypeName
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseNotifier
import com.iwdael.dbroom.annotations.UseRoom
import com.iwdael.dbroom.compiler.compat.bestGuessClassName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.regex.Pattern
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */

fun Class.roomPackage(): String {
    return this.packet.name + ""
}

fun Class.roomSimpleClassName(): String {
    return this.classSimpleName + "Room"
}

fun Class.roomClassName(): String {
    return roomPackage() + "." + roomSimpleClassName()
}

fun Class.roomTableName(): String {
    return if (getAnnotation(Entity::class.java)?.tableName?.isNotEmpty() == true) "`" + getAnnotation(
        Entity::class.java
    )!!.tableName + "`"
    else "`$classSimpleName`"
}

fun Class.roomFields(): List<Field> {
    return fields
        .filter { it.getAnnotation(Ignore::class.java) == null }
        .filter { !it.isModifier(Modifier.STATIC) }
}

fun Class.primaryKey(): Field {
    if (this.roomFields().none { it.getAnnotation(PrimaryKey::class.java) != null }) {
        throw Exception("Can not found PrimaryKey(${this.className})")
    }
    val field = roomFields().first { it.getAnnotation(PrimaryKey::class.java) != null }
    if (field.asTypeName()::class.java == TypeName::class.java) {
        throw Exception("PrimaryKey cannot be a basic type(${this.className}.${field.name})")
    }
    return field
}

fun Class.packageName(): String {
    return packet.name
}


fun Class.notifierClassName(): ClassName {
    return "${className}Notifier".bestGuessClassName().asClassName()
}


fun Class.useDataBinding(): Boolean {
    return getAnnotation(UseDataBinding::class.java) != null
}

fun Class.useRoom(): Boolean {
    return getAnnotation(UseRoom::class.java) != null
}

fun Class.useNotifier(): Boolean {
    return getAnnotation(UseNotifier::class.java) != null
}

fun Class.columnClassName(): ClassName {
    return "${this.className}SQL".asTypeName().asClassName()
}

fun Class.interfaceColumnClassName(): ClassName {
    return "${this.className}SQL.Field".asTypeName().asClassName()
}

fun Class.sqlClassName(): ClassName {
    return "${this.className}SQL".asTypeName().asClassName()
}

fun Class.sqlFinderBuilderClassName(): ClassName {
    return "${this.className}SQL.FindBuilder".asTypeName().asClassName()
}

fun Class.sqlFinderClassName(): ClassName {
    return "${this.className}SQL.Finder".asTypeName().asClassName()
}

fun Class.sqlDeleterBuilderClassName(): ClassName {
    return "${this.className}SQL.DeleteBuilder".asTypeName().asClassName()
}

fun Class.sqlDeleterClassName(): ClassName {
    return "${this.className}SQL.Deleter".asTypeName().asClassName()
}

fun Class.sqlUpdaterBuilderClassName(): ClassName {
    return "${this.className}SQL.UpdateBuilder".asTypeName().asClassName()
}

fun Class.sqlUpdaterClassName(): ClassName {
    return "${this.className}SQL.Updater".asTypeName().asClassName()
}

fun Class.sqlReplacerBuilderClassName(): ClassName {
    return "${this.className}SQL.ReplaceBuilder".asTypeName().asClassName()
}

fun Class.sqlReplacerClassName(): ClassName {
    return "${this.className}SQL.Replacer".asTypeName().asClassName()
}

fun Class.sqlInserterBuilderClassName(): ClassName {
    return "${this.className}SQL.InsertBuilder".asTypeName().asClassName()
}

fun Class.sqlInserterClassName(): ClassName {
    return "${this.className}SQL.Inserter".asTypeName().asClassName()
}


fun Class.nodeFinderClassName(): ClassName {
    return "${this.className}SQL.NodeFinder".asTypeName().asClassName()
}


fun Class.updaterClassName(): ClassName {
    return "${this.className}SQL.NodeUpdater".asTypeName().asClassName()
}

fun Class.replacerClassName(): ClassName {
    return "${this.className}SQL.NodeReplacer".asTypeName().asClassName()
}


fun Class.deleterClassName(): ClassName {
    return "${this.className}SQL.NodeDeleter".asTypeName().asClassName()
}

fun Class.findOperator(): ClassName {
    return "${this.className}SQL.NodeOperator".asTypeName().asClassName()
}

fun Class.updateOperator() = JavaClass.OPERATOR
fun Class.replaceOperator() = JavaClass.OPERATOR
fun Class.deleteOperator() = JavaClass.OPERATOR
fun Class.insertOperator() = JavaClass.OPERATOR

fun Field.orderClassName(): ClassName {
    return ClassName.bestGuess(this.parent.sqlClassName().packageName() + "." + this.parent.sqlClassName().simpleName() + "." + this.asTypeName().box().asClassName().simpleName() + "Order")
}

//fun Field.limitClassName(): ClassName {
//    return ClassName.bestGuess(this.parent.sqlClassName().packageName() + "." + this.parent.sqlClassName().simpleName() + "." + this.asTypeName().box().asClassName().simpleName() + "Limit")
//}
fun Class.limitClassName(): ClassName {
    return ClassName.bestGuess(this.sqlClassName().packageName() + "." + this.sqlClassName().simpleName() + "." + "Limit")
}

//fun Field.offsetClassName(): ClassName {
//    return ClassName.bestGuess(this.parent.sqlClassName().packageName() + "." + this.parent.sqlClassName().simpleName() + "." + this.asTypeName().box().asClassName().simpleName() + "Offset")
//}
fun Class.offsetClassName(): ClassName {
    return ClassName.bestGuess(this.sqlClassName().packageName() + "." + this.sqlClassName().simpleName() + "." + "Offset")
}

fun Class.unionClassName(): ClassName {
    return ClassName.bestGuess(this.sqlClassName().packageName() + "." + this.sqlClassName().simpleName() + "." + "Union")
}

fun Field.columnClassName(): ClassName {
    val name = when (asTypeName()) {
        TypeName.BOOLEAN -> JavaClass.BASIC_BOOLEAN_COLUMN.simpleName()
        JavaClass.BOOLEAN_PACKING -> JavaClass.PACKING_BOOLEAN_COLUMN.simpleName()
        TypeName.BYTE -> JavaClass.BASIC_BYTE_COLUMN.simpleName()
        JavaClass.BYTE_PACKING -> JavaClass.PACKING_BYTE_COLUMN.simpleName()
        TypeName.CHAR -> JavaClass.BASIC_CHAR_COLUMN.simpleName()
        JavaClass.CHAR_PACKING -> JavaClass.PACKING_CHAR_COLUMN.simpleName()
        TypeName.DOUBLE -> JavaClass.BASIC_DOUBLE_COLUMN.simpleName()
        JavaClass.DOUBLE_PACKING -> JavaClass.PACKING_DOUBLE_COLUMN.simpleName()
        TypeName.FLOAT -> JavaClass.BASIC_FLOAT_COLUMN.simpleName()
        JavaClass.FLOAT_PACKING -> JavaClass.PACKING_FLOAT_COLUMN.simpleName()
        TypeName.INT -> JavaClass.BASIC_INT_COLUMN.simpleName()
        JavaClass.INT_PACKING -> JavaClass.PACKING_INT_COLUMN.simpleName()
        TypeName.LONG -> JavaClass.BASIC_LONG_COLUMN.simpleName()
        JavaClass.LONG_PACKING -> JavaClass.PACKING_LONG_COLUMN.simpleName()
        TypeName.SHORT -> JavaClass.BASIC_SHORT_COLUMN.simpleName()
        JavaClass.SHORT_PACKING -> JavaClass.PACKING_SHORT_COLUMN.simpleName()
        ClassName.get(String::class.java) -> JavaClass.PACKING_STRING_COLUMN.simpleName()
        else -> ("Packing" + asTypeName().asClassName().simpleName() + "Column")
    }
    return ClassName.get(
        "${parent.columnClassName().packageName()}.${
            parent.columnClassName().simpleName()
        }", name
    )
}

fun Field.whereClassName(env: RoundEnvironment): ClassName {
    return when (asTypeName()) {
        TypeName.BOOLEAN -> JavaClass.CONDITION_BOOLEAN_BASIC
        JavaClass.BOOLEAN_PACKING -> JavaClass.CONDITION_BOOLEAN_PACKING
        TypeName.BYTE -> JavaClass.CONDITION_BYTE_BASIC
        JavaClass.BYTE_PACKING -> JavaClass.CONDITION_BYTE_PACKING
        TypeName.CHAR -> JavaClass.CONDITION_CHAR_BASIC
        JavaClass.CHAR_PACKING -> JavaClass.CONDITION_CHAR_PACKING
        TypeName.DOUBLE -> JavaClass.CONDITION_DOUBLE_BASIC
        JavaClass.DOUBLE_PACKING -> JavaClass.CONDITION_DOUBLE_PACKING
        TypeName.FLOAT -> JavaClass.CONDITION_FLOAT_BASIC
        JavaClass.FLOAT_PACKING -> JavaClass.CONDITION_FLOAT_PACKING
        TypeName.INT -> JavaClass.CONDITION_INTEGER_BASIC
        JavaClass.INT_PACKING -> JavaClass.CONDITION_INTEGER_PACKING
        TypeName.LONG -> JavaClass.CONDITION_LONG_BASIC
        JavaClass.LONG_PACKING -> JavaClass.CONDITION_LONG_PACKING
        TypeName.SHORT -> JavaClass.CONDITION_SHORT_BASIC
        JavaClass.SHORT_PACKING -> JavaClass.CONDITION_SHORT_PACKING
        ClassName.get(String::class.java) -> JavaClass.CONDITION_STRING_PACKING
        else -> {
            ClassName.get(this.parent.sqlClassName().packageName() + "." + this.parent.sqlClassName().simpleName(), "SqlUnitPacking${this.asTypeName().asClassName().simpleName()}")
        }
    }
}

fun Field.convertType(env: RoundEnvironment): Pair<Field, Method>? {
    return arrayOf(this).filter { it.asTypeName().box() != JavaClass.BOOLEAN_PACKING }
        .filter { it.asTypeName().box() != JavaClass.BYTE_PACKING }
        .filter { it.asTypeName().box() != JavaClass.CHAR_PACKING }
        .filter { it.asTypeName().box() != JavaClass.SHORT_PACKING }
        .filter { it.asTypeName().box() != JavaClass.INT_PACKING }
        .filter { it.asTypeName().box() != JavaClass.LONG_PACKING }
        .filter { it.asTypeName().box() != JavaClass.DOUBLE_PACKING }
        .filter { it.asTypeName().box() != JavaClass.FLOAT_PACKING }
        .filter { it.asTypeName().box() != ClassName.get(String::class.java) }
        .map { field ->
            val method = env.getElementsAnnotatedWith(TypeConverter::class.java)
                .map { Method(it) }
                .filter {
                    Pattern.compile("value=(.*)\\)")
                        .matcher(this.parent.getAnnotation(TypeConverters::class.java).toString())
                        .let {
                            if (it.find()) {
                                it.group(1).trim().split(",").toTypedArray()
                            } else {
                                arrayOf()
                            }
                        }
                        .contains(it.parent.className)
                }
                .filter { it.parameter.size == 1 }
                .filter { it.parameter.get(0).asTypeName() == field.asTypeName() }
                .getOrNull(0) ?: return null
            field to method
        }
        .getOrNull(0)
}


fun TypeName.convertType(clazz: Class, env: RoundEnvironment): Pair<TypeName, Method>? {
    return arrayOf(this).filter { it.box() != JavaClass.BOOLEAN_PACKING }
        .filter { it.box() != JavaClass.BYTE_PACKING }
        .filter { it.box() != JavaClass.CHAR_PACKING }
        .filter { it.box() != JavaClass.SHORT_PACKING }
        .filter { it.box() != JavaClass.INT_PACKING }
        .filter { it.box() != JavaClass.LONG_PACKING }
        .filter { it.box() != JavaClass.DOUBLE_PACKING }
        .filter { it.box() != JavaClass.FLOAT_PACKING }
        .filter { it.box() != ClassName.get(String::class.java) }
        .map { field ->
            val method = env.getElementsAnnotatedWith(TypeConverter::class.java)
                .map { Method(it) }
                .filter {
                    Pattern.compile("value=(.*)\\)")
                        .matcher(clazz.getAnnotation(TypeConverters::class.java).toString())
                        .let {
                            if (it.find()) {
                                it.group(1).trim().split(",").toTypedArray()
                            } else {
                                arrayOf()
                            }
                        }
                        .contains(it.parent.className)
                }
                .filter { it.parameter.size == 1 }
                .filter { it.parameter.get(0).asTypeName() == field }
                .getOrNull(0) ?: return null
            field to method
        }
        .getOrNull(0)
}