package com.iwdael.dbroom.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
object JavaClass {
    const val DEBUG = false
    var MASTER_PACKAGE = "com.iwdael.dbroom"
    var DB_ROOM_SIMPLE_NAME = "DbRoom"
    val BASE_NOTIFIER: ClassName = ClassName.get("com.iwdael.dbroom.core", "Notifier")
    val ROOM_DATABASE: ClassName = ClassName.get("androidx.room", "RoomDatabase")
    val CONTEXT: ClassName = ClassName.get("android.content", "Context")
    val LOGGER: ClassName = ClassName.get("android.util", "Log")
    val PACKING_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingColumn")
    val BASIC_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicColumn")
    val BASIC_CHAR_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicCharColumn")
    val PACKING_CHAR_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingCharColumn")

    val BASIC_BOOLEAN_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicBooleanColumn")
    val PACKING_BOOLEAN_COLUMN: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "PackingBooleanColumn")

    val BASIC_SHORT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicShortColumn")
    val PACKING_SHORT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingShortColumn")

    val BASIC_BYTE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicByteColumn")
    val PACKING_BYTE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingByteColumn")

    val BASIC_INT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicIntColumn")
    val PACKING_INT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingIntColumn")

    val BASIC_LONG_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicLongColumn")
    val PACKING_LONG_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingLongColumn")

    val BASIC_FLOAT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicFloatColumn")
    val PACKING_FLOAT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingFloatColumn")

    val BASIC_DOUBLE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "BasicDoubleColumn")
    val PACKING_DOUBLE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingDoubleColumn")

    val PACKING_STRING_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "PackingStringColumn")


    val COLUMN: ClassName = ClassName.get("com.iwdael.dbroom.core", "Column")
    val UTILS: ClassName = ClassName.get("com.iwdael.dbroom.core", "Utils")
    val OPERATOR: ClassName = ClassName.get("com.iwdael.dbroom.core", "Operator")
    val CONDITION: ClassName = ClassName.get("com.iwdael.dbroom.core", "Condition")
    val CONDITION_STRING_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingString")

    val CONDITION_INTEGER_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingInteger")
    val CONDITION_INTEGER_BASIC: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionBasicInteger")

    val CONDITION_FLOAT_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingFloat")
    val CONDITION_FLOAT_BASIC: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBasicFloat")

    val CONDITION_SHORT_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingShort")
    val CONDITION_SHORT_BASIC: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBasicShort")

    val CONDITION_CHAR_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingChar")
    val CONDITION_CHAR_BASIC: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBasicChar")

    val CONDITION_DOUBLE_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingDouble")
    val CONDITION_DOUBLE_BASIC: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionBasicDouble")

    val CONDITION_LONG_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingLong")
    val CONDITION_LONG_BASIC: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBasicLong")

    val CONDITION_BYTE_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingByte")
    val CONDITION_BYTE_BASIC: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBasicByte")

    val CONDITION_BOOLEAN_PACKING: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionPackingBoolean")
    val CONDITION_BOOLEAN_BASIC: ClassName =
        ClassName.get("com.iwdael.dbroom.core", "ConditionBasicBoolean")

    val CONDITION_BUILDER: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBuilder")
    val CONDITION_BUILDER_2: ClassName = ClassName.get("com.iwdael.dbroom.core", "ConditionBuilder2")
    val CREATOR: ClassName = ClassName.get("com.iwdael.dbroom.core", "Creator")
    val NEXT_BUILDER: ClassName = ClassName.get("com.iwdael.dbroom.core", "NextBuilder")
    val CALLBACK: ClassName = ClassName.get("com.iwdael.dbroom.core", "CallBack")

    val FLOAT_PACKING: ClassName = ClassName.get("java.lang", "Float")
    val INT_PACKING: ClassName = ClassName.get("java.lang", "Integer")
    val SHORT_PACKING: ClassName = ClassName.get("java.lang", "Short")
    val CHAR_PACKING: ClassName = ClassName.get("java.lang", "Character")
    val DOUBLE_PACKING: ClassName = ClassName.get("java.lang", "Double")
    val LONG_PACKING: ClassName = ClassName.get("java.lang", "Long")
    val BYTE_PACKING: ClassName = ClassName.get("java.lang", "Byte")
    val BOOLEAN_PACKING: ClassName = ClassName.get("java.lang", "Boolean")

    val FLOAT_BASIC: TypeName = ClassName.FLOAT
    val INT_BASIC: TypeName = ClassName.INT
    val SHORT_BASIC: TypeName = ClassName.SHORT
    val CHAR_BASIC: TypeName = ClassName.CHAR
    val DOUBLE_BASIC: TypeName = ClassName.DOUBLE
    val LONG_BASIC: TypeName = ClassName.LONG
    val BYTE_BASIC: TypeName = ClassName.BYTE
    val BOOLEAN_BASIC: TypeName = ClassName.BOOLEAN


    val STRING: ClassName = ClassName.get(String::class.java)

    val STORE_ROOM: ClassName get() = ClassName.get(MASTER_PACKAGE, "StoreRoom")
    val CONVERTER: ClassName get() = ClassName.get(MASTER_PACKAGE, "Converter")
    val DB_ROOM: ClassName get() = ClassName.get(MASTER_PACKAGE, DB_ROOM_SIMPLE_NAME)
    val STORE: ClassName = ClassName.get("com.iwdael.dbroom.core", "Store")
    val ROOM_NOTIFIER: ClassName = ClassName.get("com.iwdael.dbroom.core", "RoomNotifier")
    val ROOM_NOTIFIER_NOTIFIER: ClassName =
        ClassName.get("${ROOM_NOTIFIER.packageName()}.${ROOM_NOTIFIER.simpleName()}", "Notifier")

    val BR: ClassName = ClassName.get("androidx.databinding.library.baseAdapters", "BR")
    val DB: ClassName get() = ClassName.get(MASTER_PACKAGE, "DB")

    const val INDENT = "    "
}