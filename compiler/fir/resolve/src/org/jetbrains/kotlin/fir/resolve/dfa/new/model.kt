/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.dfa.new

import com.google.common.collect.Multimap
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.modality
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.resolve.dfa.Condition
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

// --------------------------------------- Variables ---------------------------------------

sealed class DataFlowVariable(private val variableIndexForDebug: Int) {
    abstract val isStable: Boolean

    final override fun toString(): String {
        return "d$variableIndexForDebug"
    }
}

class RealVariable(
    val symbol: AbstractFirBasedSymbol<*>,
    val isThisReference: Boolean,
    val receiverVariable: DataFlowVariable?,
    val isSafeCall: Boolean,
    variableIndexForDebug: Int
) : DataFlowVariable(variableIndexForDebug) {
    override val isStable: Boolean by lazy {
        when (symbol) {
            is FirPropertySymbol -> {
                val property = symbol.fir
                when {
                    property.isLocal -> true
                    property.isVar -> false
                    property.modality != Modality.FINAL -> false
                    else -> true
                }
            }
            else -> true
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RealVariable

        if (symbol != other.symbol) return false
        if (receiverVariable != other.receiverVariable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + (receiverVariable?.hashCode() ?: 0)
        return result
    }
}

class SyntheticVariable(val fir: FirElement, variableIndexForDebug: Int) : DataFlowVariable(variableIndexForDebug) {
    override val isStable: Boolean get() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SyntheticVariable

        if (fir != other.fir) return false

        return true
    }

    override fun hashCode(): Int {
        return fir.hashCode()
    }
}

// --------------------------------------- Facts ---------------------------------------

sealed class PredicateEffect<T : PredicateEffect<T>> {
    abstract fun invert(): T
}

data class Predicate(val variable: DataFlowVariable, val condition: Condition) : PredicateEffect<Predicate>() {
    override fun invert(): Predicate {
        return Predicate(variable, condition.invert())
    }

    override fun toString(): String {
        return "$variable $condition"
    }
}

abstract class DataFlowInfo : PredicateEffect<DataFlowInfo>() {
    abstract val variable: RealVariable
    abstract val exactType: Set<ConeKotlinType>
    abstract val exactNotType: Set<ConeKotlinType>

    abstract operator fun plus(other: DataFlowInfo): DataFlowInfo
    abstract val isEmpty: Boolean
    val isNotEmpty: Boolean get() = !isEmpty

    override fun toString(): String {
        return "$variable: $exactType, $exactNotType"
    }
}

class MutableDataFlowInfo(
    override val variable: RealVariable,
    override val exactType: MutableSet<ConeKotlinType> = HashSet(),
    override val exactNotType: MutableSet<ConeKotlinType> = HashSet()
) : DataFlowInfo() {
    override fun plus(other: DataFlowInfo): MutableDataFlowInfo = MutableDataFlowInfo(
        variable,
        HashSet(exactType).apply { addAll(other.exactType) },
        HashSet(exactNotType).apply { addAll(other.exactNotType) }
    )

    override val isEmpty: Boolean
        get() = exactType.isEmpty() && exactType.isEmpty()

    override fun invert(): DataFlowInfo {
        return MutableDataFlowInfo(
            variable,
            HashSet(exactNotType),
            HashSet(exactType)
        )
    }

    operator fun plusAssign(info: DataFlowInfo) {
        exactType += info.exactType
        exactNotType += info.exactNotType
    }

    fun copy(): MutableDataFlowInfo = MutableDataFlowInfo(variable, HashSet(exactType), HashSet(exactNotType))
}

class LogicStatement(
    val condition: Predicate,
    val effect: PredicateEffect<*>
) {
    override fun toString(): String {
        return "$condition -> $effect"
    }
}

fun LogicStatement.invertCondition(): LogicStatement = LogicStatement(condition.invert(), effect)

// --------------------------------------- Aliases ---------------------------------------

typealias KnownInfos = Map<RealVariable, DataFlowInfo>
typealias MutableKnownFacts = MutableMap<RealVariable, MutableDataFlowInfo>
typealias LogicStatements = Multimap<Predicate, LogicStatement>

// --------------------------------------- DSL ---------------------------------------

infix fun DataFlowVariable.eq(constant: Boolean?): Predicate {
    val condition = when (constant) {
        true -> Condition.EqTrue
        false -> Condition.EqFalse
        null -> Condition.EqNull
    }
    return Predicate(this, condition)
}

infix fun DataFlowVariable.notEq(constant: Boolean?): Predicate {
    val condition = when (constant) {
        true -> Condition.EqFalse
        false -> Condition.EqTrue
        null -> Condition.NotEqNull
    }
    return Predicate(this, condition)
}

infix fun Predicate.implies(effect: PredicateEffect<*>): LogicStatement = LogicStatement(this, effect)

infix fun RealVariable.has(types: MutableSet<ConeKotlinType>): DataFlowInfo = MutableDataFlowInfo(this, types, HashSet())
infix fun RealVariable.has(type: ConeKotlinType): DataFlowInfo =
    MutableDataFlowInfo(this, HashSet<ConeKotlinType>().apply { this += type }, HashSet())

infix fun RealVariable.hasNot(types: MutableSet<ConeKotlinType>): DataFlowInfo = MutableDataFlowInfo(this, HashSet(), types)
infix fun RealVariable.hasNot(type: ConeKotlinType): DataFlowInfo =
    MutableDataFlowInfo(this, HashSet(), HashSet<ConeKotlinType>().apply { this += type })