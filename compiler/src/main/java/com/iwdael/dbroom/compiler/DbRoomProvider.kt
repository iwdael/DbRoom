package com.iwdael.dbroom.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class DbRoomProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        environment.logger.warn("DbRoomProvider[${this.hashCode()}]")
        return DbRoomProcessor(environment)
    }
}