/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.dfa.new

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol

class VariableStorage {
    fun removeRealVariable(symbol: AbstractFirBasedSymbol<*>) {
        TODO("not implemented")
    }

    fun removeSyntheticVariable(variable: DataFlowVariable) {
        TODO()
    }

    fun reset() {
        TODO("not implemented")
    }

    fun getOrCreateRealVariable(symbol: AbstractFirBasedSymbol<*>): RealVariable = TODO()

    @JvmName("getOrCreateRealVariableOrNull")
    fun getOrCreateRealVariable(symbol: AbstractFirBasedSymbol<*>?): RealVariable? = symbol?.let { getOrCreateRealVariable(it) }
    fun createSyntheticVariable(fir: FirElement): SyntheticVariable = TODO()
    fun getOrCreateVariable(fir: FirElement): DataFlowVariable = TODO()

    /**
     * Also removes variable for [symbol] if it exists
     * If pass [null] as [existingVariable] then [VariableStorage] tries to
     *   find [existingVariable] by itself
     */
    fun attachSymbolToVariable(symbol: AbstractFirBasedSymbol<*>, variable: RealVariable, existingVariable: DataFlowVariable? = null) {
        TODO()
    }

    operator fun get(symbol: AbstractFirBasedSymbol<*>?): RealVariable? = TODO()
    operator fun get(fir: FirElement): DataFlowVariable? = TODO()
}