/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.dfa.new

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSymbolOwner
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.FirThisReference
import org.jetbrains.kotlin.fir.references.impl.FirExplicitThisReference
import org.jetbrains.kotlin.fir.resolve.calls.FirNamedReferenceWithCandidate
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol

class VariableStorage {
    private var counter = 0
    private val realVariables: MutableMap<AbstractFirBasedSymbol<*>, RealVariable> = HashMap()
    private val syntheticVariables: MutableMap<FirElement, SyntheticVariable> = HashMap()
    private val localVariableAliases: MutableMap<AbstractFirBasedSymbol<*>, AbstractFirBasedSymbol<*>> = HashMap()

    fun getOrCreateRealVariable(symbol: AbstractFirBasedSymbol<*>): RealVariable {
        return realVariables.getOrPut(symbol) { createRealVariableInternal(symbol.fir) }
    }

    private fun createRealVariableInternal(fir: FirSymbolOwner<*>): RealVariable {
        val receiver: FirExpression?
        val isSafeCall: Boolean
        val isThisReference: Boolean
        when (val expression = fir as? FirQualifiedAccessExpression) {
            null -> {
                receiver = null
                isSafeCall = false
                isThisReference = false
            }
            else -> {
                receiver = expression.explicitReceiver
                isSafeCall = expression.safe
                isThisReference = expression.calleeReference is FirThisReference
            }
        }
        val receiverVariable = receiver?.let { getOrCreateVariable(it) }
        return RealVariable(fir.symbol, isThisReference, receiverVariable, isSafeCall, counter++)
    }

    @JvmName("getOrCreateRealVariableOrNull")
    fun getOrCreateRealVariable(symbol: AbstractFirBasedSymbol<*>?): RealVariable? = symbol?.let { getOrCreateRealVariable(it) }

    fun createSyntheticVariable(fir: FirElement): SyntheticVariable =
        SyntheticVariable(fir, counter++).also { syntheticVariables[fir] = it }

    fun getOrCreateVariable(fir: FirElement): DataFlowVariable {
        return when (val symbol = fir.symbol) {
            null -> syntheticVariables[fir] ?: createSyntheticVariable(fir)
            else -> getOrCreateRealVariable(symbol)
        }
    }

    /**
     * Also removes existing real variable for [varSymbol] if it exists
     */
    fun attachSymbolToVariable(varSymbol: AbstractFirBasedSymbol<*>, targetVariable: RealVariable) {
        localVariableAliases[varSymbol] = targetVariable.symbol
        realVariables.remove(varSymbol)
    }

    operator fun get(symbol: AbstractFirBasedSymbol<*>?): RealVariable? =
        symbol?.let { realVariables[localVariableAliases[it] ?: it] }

    operator fun get(fir: FirElement): DataFlowVariable? {
        val symbol = fir.symbol
        return if (symbol != null) {
            get(symbol)
        } else {
            syntheticVariables[fir]
        }
    }

    fun removeRealVariable(symbol: AbstractFirBasedSymbol<*>) {
        assert(!localVariableAliases.containsValue(symbol))
        realVariables.remove(symbol)
    }

    fun removeSyntheticVariable(variable: DataFlowVariable) {
        if (variable !is SyntheticVariable) return
        syntheticVariables.remove(variable.fir)
    }

    fun reset() {
        counter = 0
        realVariables.clear()
        syntheticVariables.clear()
        localVariableAliases.clear()
    }
}

internal val FirElement.symbol: AbstractFirBasedSymbol<*>?
    get() = when (this) {
        is FirResolvable -> symbol
        is FirSymbolOwner<*> -> symbol
        else -> null
    }

internal val FirResolvable.symbol: AbstractFirBasedSymbol<*>?
    get() = when (val reference = calleeReference) {
        is FirExplicitThisReference -> reference.boundSymbol
        is FirResolvedNamedReference -> reference.resolvedSymbol
        is FirNamedReferenceWithCandidate -> reference.candidateSymbol
        else -> null
    }