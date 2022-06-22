package com.iwdael.dbroom.example

import com.iwdael.dbroom.example.entity.AirTechSQL
import com.iwdael.dbroom.example.entity.VRSQL


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
    println(finder1.selection)


    val finder2 = AirTechSQL
        .newFinder()
        .fields()
        .order(AirTechSQL.int_)
        .desc()
        .limit(10)
        .offset(10)
        .build()
    println(finder2.selection)


    val finder3 = AirTechSQL
        .newFinder()
        .fields()
        .limit(10)
        .build()
    println(finder3.selection)

    val finder4 = AirTechSQL
        .newFinder()
        .fields()
        .limit(10)
        .offset(10)
        .build()
    println(finder4.selection)

    val finder5 = AirTechSQL
        .newFinder()
        .fields()
        .order(AirTechSQL.int_)
        .asc()
        .build()
    println(finder5.selection)


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
    println(finder6.selection)

    val finder7 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .order(AirTechSQL.key)
        .asc()
        .limit(10)
        .build()
    println(finder7.selection)

    val finder8 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .order(AirTechSQL.key)
        .asc()
        .build()
    println(finder8.selection)

    val finder9 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .build()
    println(finder9.selection)

    val finder10 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .limit(10)
        .build()
    println(finder10.selection)

    val finder11 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .offset(10)
        .build()
    println(finder11.selection)

    val finder12 = AirTechSQL
        .newFinder()
        .fields(AirTechSQL.long_, AirTechSQL.short_)
        .where(AirTechSQL.int_)
        .equal(10)
        .limit(10)
        .offset(10)
        .build()
    println(finder12.selection)



}