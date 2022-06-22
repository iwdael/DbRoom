package com.iwdael.dbroom.example

import com.iwdael.dbroom.example.entity.AirTechSQL


/**
 * @author  : iwdael
 * @mail    : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */

fun main() {
    val finder1 = AirTechSQL
        .newFinder()
        .fields()
        .order(AirTechSQL.int_)
        .desc()
        .limit(10)
        .offset(10)
        .build()
    println(finder1.selection + "    <>      " + finder1.bindArgs.joinToString(separator = ","))


    val finder2 = AirTechSQL
        .newFinder()
        .fields()
        .order(AirTechSQL.int_)
        .desc()
        .limit(10)
        .offset(10)
        .build()
    println(finder2.selection + "    <>      " + finder2.bindArgs.joinToString(separator = ","))


    val finder3 = AirTechSQL
        .newFinder()
        .fields()
        .limit(10)
        .build()
    println(finder3.selection + "    <>      " + finder3.bindArgs.joinToString(separator = ","))

    val finder4 = AirTechSQL
        .newFinder()
        .fields()
        .limit(10)
        .offset(10)
        .build()
    println(finder4.selection + "    <>      " + finder4.bindArgs.joinToString(separator = ","))

    val finder5 = AirTechSQL
        .newFinder()
        .fields()
        .order(AirTechSQL.int_)
        .asc()
        .build()
    println(finder5.selection + "    <>      " + finder5.bindArgs.joinToString(separator = ","))


    val finder6 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .order(AirTechSQL.key)
        .asc()
        .limit(10)
        .offset(10)
        .build()
    println(finder6.selection + "    <>      " + finder6.bindArgs.joinToString(separator = ","))

    val finder7 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .order(AirTechSQL.key)
        .asc()
        .limit(10)
        .build()
    println(finder7.selection + "    <>      " + finder7.bindArgs.joinToString(separator = ","))

    val finder8 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .order(AirTechSQL.key)
        .asc()
        .build()
    println(finder8.selection + "    <>      " + finder8.bindArgs.joinToString(separator = ","))

    val finder9 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .build()
    println(finder9.selection + "    <>      " + finder9.bindArgs.joinToString(separator = ","))

    val finder10 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .limit(10)
        .build()
    println(finder10.selection + "    <>      " + finder10.bindArgs.joinToString(separator = ","))

    val finder11 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .offset(10)
        .build()
    println(finder11.selection + "    <>      " + finder11.bindArgs.joinToString(separator = ","))

    val finder12 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .limit(10)
        .offset(10)
        .build()
    println(finder12.selection + "    <>      " + finder12.bindArgs.joinToString(separator = ","))


    val updater1 = AirTechSQL.newUpdater()
        .appending(AirTechSQL.int_,10)
        .appending(AirTechSQL.long_,49)
        .appended(AirTechSQL.float_,0f)
        .where(AirTechSQL.key)
        .equal(10)
        .build()

    println(updater1.selection + "    <>      " + updater1.bindArgs.joinToString(separator = ","))


    val deleter1 = AirTechSQL.newDeleter()
        .fields()
        .where(AirTechSQL.key)
        .equal(10)
        .build()
    println(deleter1.selection + "    <>      " + deleter1.bindArgs.joinToString(separator = ","))

    val deleter2 = AirTechSQL.newDeleter()
        .fields(AirTechSQL.long_,AirTechSQL.short_)
        .where(AirTechSQL.key)
        .equal(10)
        .build()
    println(deleter2.selection + "    <>      " + deleter2.bindArgs.joinToString(separator = ","))


    val deleter3 = AirTechSQL.newDeleter()
        .fields(AirTechSQL.long_,AirTechSQL.short_)
        .where(AirTechSQL.key)
        .equal(10)
        .or()
        .where(AirTechSQL.long_)
        .equal(7)
        .build()
    println(deleter3.selection + "    <>      " + deleter3.bindArgs.joinToString(separator = ","))


    val replacer1 = AirTechSQL.newReplacer()
        .appending(AirTechSQL.int_,1)
        .appending(AirTechSQL.long_,2)
        .appending(AirTechSQL.float_,3f)
        .appended(AirTechSQL.key,1)
        .where(AirTechSQL.key)
        .equal(1)
        .build()
    println("-----")
    println(replacer1.findSelection + "    <>      " + replacer1.findBindArgs.joinToString(separator = ","))
    println(replacer1.insertSelection + "    <>      " + replacer1.insertBindArgs.joinToString(separator = ","))
    println(replacer1.updateSelection + "    <>      " + replacer1.updateBindArgs.joinToString(separator = ","))
    println("-----")
}