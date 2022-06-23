package com.iwdael.dbroom.compiler.compat

import com.squareup.javapoet.JavaFile
import java.lang.Exception
import javax.annotation.processing.Filer

/**
 * author : 段泽全(hacknife)
 * e-mail : hacknife@outlook.com
 * time   : 2019/8/5
 * desc   : MVVM
 * version: 1.0
 */
fun JavaFile.write(filer: Filer) {
    try {
        this.writeTo(filer)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}