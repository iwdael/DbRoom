package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.Generator
import javax.annotation.processing.Filer


class RoomKtMaker(private val generator: Generator) : Maker {
    override fun classFull() = "${Maker.ROOT_PACKAGE}.${className()}"
    override fun className() = "${generator.className}RoomCompat"
    override fun packageName() = Maker.ROOT_PACKAGE
    override fun make(filer: Filer) {
    }


}