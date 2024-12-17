package com.iwdael.dbroom.compiler.compat

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.Import
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * @author iwdael
 * @since 2024/12/17
 * @desc this is FunSpecExtension
 */
fun FunSpec.Builder.coroutinesFunModifierCompatible() = apply {
    if (ENABLED_COROUTINES) this.addModifiers(KModifier.SUSPEND)
}

fun FileSpec.Builder.coroutinesImportCompatible() = apply {
    if (!ENABLED_COROUTINES) return@apply
    addImport("kotlinx.coroutines.sync", "withLock")
    addImport("kotlinx.coroutines", "withContext", "Dispatchers")
}

fun FunSpec.Builder.coroutinesInstanceFunCompatible() = apply {
    if (ENABLED_COROUTINES) {
        beginControlFlow("if (instance == null)")
        beginControlFlow("return withContext(Dispatchers.Default)")
        beginControlFlow("initLock.withLock")
        beginControlFlow("if (instance == null)")
        addStatement("return@withContext deferred.await()")
        nextControlFlow("else")
        addStatement("return@withContext instance!!")
        endControlFlow()
        endControlFlow()
        endControlFlow()
        nextControlFlow("else")
        addStatement("return instance!!")
        endControlFlow()
    } else {
        addStatement("if (instance == null) throw RuntimeException(\"Please initialize DbRoom first\")")
        addStatement("return instance!!")
    }
}

fun TypeSpec.Builder.coroutinesPropertyCompatible() = apply {
    if (!ENABLED_COROUTINES) return@apply
    val classNameMutex = ClassName.bestGuess("kotlinx.coroutines.sync.Mutex")
    val classNameCompletableDeferred = ClassName.bestGuess("kotlinx.coroutines.CompletableDeferred")
    addProperty(
        PropertySpec.builder("initLock", classNameMutex)
            .addModifiers(KModifier.PRIVATE)
            .initializer("%T()", classNameMutex)
            .build()
    )
    addProperty(
        PropertySpec.builder("deferred", classNameCompletableDeferred.parameterizedBy(DB_ROOM))
            .addModifiers(KModifier.PRIVATE)
            .initializer("%T()", classNameCompletableDeferred)
            .build()
    )
}

fun CodeBlock.Builder.beginControlFlowCoroutinesDbRoomCompatible(name: String) = apply {
    if (ENABLED_COROUTINES) {
        beginControlFlow("initLock.withLock")
    } else {
        beginControlFlow("synchronized ($name::class)")
    }
}

fun CodeBlock.Builder.endControlFlowCoroutinesDbRoomCompatible() = apply {
    if (ENABLED_COROUTINES) {
        addStatement("deferred.complete(instance!!)")
        endControlFlow()
    } else {
        endControlFlow()
    }
}