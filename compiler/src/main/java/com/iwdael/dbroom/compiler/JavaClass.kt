package com.iwdael.dbroom.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/dbroom
 */
object JavaClass {
    val BASE_OBSERVABLE: ClassName = ClassName.bestGuess("com.iwdael.dbroom.core.BaseObservable")
    val ROOM_OBSERVABLE: ClassName = ClassName.get("com.iwdael.dbroom.core", "RoomObservable")
    val ROOM_DATABASE: ClassName = ClassName.get("androidx.room", "RoomDatabase")
    val CONTEXT: ClassName = ClassName.get("android.content", "Context")
    val ROOM_OBSERVABLE_CREATOR: ClassName =
        ClassName.get("com.iwdael.dbroom", "RoomObservableCreator")
    val LOGGER: ClassName = ClassName.get("com.iwdael.dbroom.core", "Logger")
    val PACKING_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingColumn")
    val BASIC_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicColumn")
    val BASIC_CHAR_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicCharColumn")
    val PACKING_CHAR_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingCharColumn")

    val BASIC_BOOLEAN_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicBooleanColumn")
    val PACKING_BOOLEAN_COLUMN: ClassName =
        ClassName.get("com.iwdael.dbroom", "PackingBooleanColumn")

    val BASIC_SHORT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicShortColumn")
    val PACKING_SHORT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingShortColumn")

    val BASIC_BYTE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicByteColumn")
    val PACKING_BYTE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingByteColumn")

    val BASIC_INT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicIntColumn")
    val PACKING_INT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingIntColumn")

    val BASIC_LONG_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicLongColumn")
    val PACKING_LONG_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingLongColumn")

    val BASIC_FLOAT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicFloatColumn")
    val PACKING_FLOAT_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingFloatColumn")

    val BASIC_DOUBLE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "BasicDoubleColumn")
    val PACKING_DOUBLE_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingDoubleColumn")

    val PACKING_STRING_COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "PackingStringColumn")


    val COLUMN: ClassName = ClassName.get("com.iwdael.dbroom", "Column")
    val UTILS: ClassName = ClassName.get("com.iwdael.dbroom", "Utils")
    val OPERATOR: ClassName = ClassName.get("com.iwdael.dbroom", "Operator")
    val CONDITION: ClassName = ClassName.get("com.iwdael.dbroom", "Condition")
    val WHERE_STRING_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingString")

    val WHERE_INTEGER_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingInteger")
    val WHERE_INTEGER_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicInteger")

    val WHERE_FLOAT_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingFloat")
    val WHERE_FLOAT_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicFloat")

    val WHERE_SHORT_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingShort")
    val WHERE_SHORT_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicShort")

    val WHERE_CHAR_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingChar")
    val WHERE_CHAR_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicChar")

    val WHERE_DOUBLE_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingDouble")
    val WHERE_DOUBLE_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicDouble")

    val WHERE_LONG_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingLong")
    val WHERE_LONG_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicLong")

    val WHERE_BYTE_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingByte")
    val WHERE_BYTE_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicByte")

    val WHERE_BOOLEAN_PACKING: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionPackingBoolean")
    val WHERE_BOOLEAN_BASIC: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBasicBoolean")

    val CONDITION_BUILDER: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBuilder")
    val CONDITION_BUILDER_2: ClassName = ClassName.get("com.iwdael.dbroom", "ConditionBuilder2")
    val CREATOR: ClassName = ClassName.get("com.iwdael.dbroom", "Creator")
    val NEXT_BUILDER: ClassName = ClassName.get("com.iwdael.dbroom", "NextBuilder")
    val CALLBACK: ClassName = ClassName.get("com.iwdael.dbroom", "CallBack")

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
}