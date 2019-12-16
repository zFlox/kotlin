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
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol

class VariableStorage {
    private var counter = 1
    private val realVariables: MutableMap<AbstractFirBasedSymbol<*>, RealVariable> = HashMap()
    private val syntheticVariables: MutableMap<FirElement, SyntheticVariable> = HashMap()
    private val localVariableAliases: MutableMap<AbstractFirBasedSymbol<*>, AbstractFirBasedSymbol<*>> = HashMap()

    fun getOrCreateRealVariable(symbol: AbstractFirBasedSymbol<*>, fir: FirElement): RealVariable {
        return realVariables.getOrPut(symbol) { createRealVariableInternal(symbol.fir, fir) }
    }

    private fun createRealVariableInternal(declaration: FirSymbolOwner<*>, originalFir: FirElement): RealVariable {
        val receiver: FirExpression?
        val isSafeCall: Boolean
        val isThisReference: Boolean
        val expression = when (originalFir) {
            is FirQualifiedAccessExpression -> originalFir
            is FirWhenSubjectExpression -> originalFir.whenSubject.whenExpression.subject as? FirQualifiedAccessExpression
            else -> null
        }

        if (expression != null) {
            receiver = expression.explicitReceiver
            isSafeCall = expression.safe
            isThisReference = expression.calleeReference is FirThisReference
        } else {
            receiver = null
            isSafeCall = false
            isThisReference = false
        }

        val receiverVariable = receiver?.let { getOrCreateVariable(it) }
        return RealVariable(declaration.symbol, isThisReference, receiverVariable, isSafeCall, counter++)
    }

    @JvmName("getOrCreateRealVariableOrNull")
    fun getOrCreateRealVariable(symbol: AbstractFirBasedSymbol<*>?, fir: FirElement): RealVariable? =
        symbol?.let { getOrCreateRealVariable(it, fir) }

    fun createSyntheticVariable(fir: FirElement): SyntheticVariable =
        SyntheticVariable(fir, counter++).also { syntheticVariables[fir] = it }

    fun getOrCreateVariable(fir: FirElement): DataFlowVariable {
        return when (val symbol = fir.symbol) {
            null -> syntheticVariables[fir] ?: createSyntheticVariable(fir)
            else -> getOrCreateRealVariable(symbol, fir)
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
        // TODO: this shit fails
//        assert(!localVariableAliases.containsValue(symbol))
        realVariables.remove(symbol)
    }

    fun unboundPossiblyAliasedVariable(symbol: AbstractFirBasedSymbol<*>): RealVariable {
        val aliasedSymbol = localVariableAliases.remove(symbol)
        return getOrCreateRealVariable(symbol, symbol.fir)
    }

    fun removeRealVariable(variable: RealVariable) {
        val aliasSymbol = localVariableAliases.remove(variable.symbol)
        val removedVariable = realVariables.remove(variable.symbol)
        assert((aliasSymbol != null) xor (removedVariable != null))
        assert(removedVariable == null || removedVariable == variable)
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
        is FirWhenSubjectExpression -> whenSubject.whenExpression.subject?.symbol
        else -> null
    }?.takeIf { it !is FirFunctionSymbol<*> }

internal val FirResolvable.symbol: AbstractFirBasedSymbol<*>?
    get() = when (val reference = calleeReference) {
        is FirExplicitThisReference -> reference.boundSymbol
        is FirResolvedNamedReference -> reference.resolvedSymbol
        is FirNamedReferenceWithCandidate -> reference.candidateSymbol
        else -> null
    }