/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.dfa.new

import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.UniversalConeInferenceContext
import org.jetbrains.kotlin.fir.types.ConeKotlinType

abstract class Flow {
    abstract fun getKnownInfo(variable: RealVariable): DataFlowInfo?
    abstract fun getDfaConditions(variable: DataFlowVariable): Collection<LogicStatement>
    abstract fun getVariablesInKnownInfos(): Collection<RealVariable>
    abstract fun removeConditions(variable: DataFlowVariable): Collection<LogicStatement>
}

fun KnownInfos.intersect(other: KnownInfos): MutableKnownFacts = TODO()

abstract class LogicSystem<FLOW : Flow>(private val context: UniversalConeInferenceContext) {
    // ------------------------------- Flow operations -------------------------------

    abstract fun createEmptyFlow(): FLOW
    abstract fun forkFlow(flow: FLOW): FLOW
    abstract fun joinFlow(flows: Collection<FLOW>): FLOW

    abstract fun addKnownInfo(flow: FLOW, info: DataFlowInfo)

    abstract fun addLogicStatement(flow: FLOW, statement: LogicStatement)

    abstract fun removeAllAboutVariable(flow: FLOW, variable: RealVariable)

    abstract fun translateConditionalVariableInStatements(
        flow: FLOW,
        originalVariable: DataFlowVariable,
        newVariable: DataFlowVariable,
        shouldRemoveOriginalStatements: Boolean,
        filter: (LogicStatement) -> Boolean = { true },
        transform: (LogicStatement) -> LogicStatement = { it }
    )

    abstract fun approveStatementsInsideFlow(
        flow: FLOW,
        predicate: Predicate,
        shouldForkFlow: Boolean,
        shouldRemoveSynthetics: Boolean
    ): FLOW

    // ------------------------------- Callbacks for updating implicit receiver stack -------------------------------

    abstract fun processUpdatedReceiverVariable(flow: FLOW, variable: RealVariable)
    abstract fun updateAllReceivers(flow: FLOW)

    // ------------------------------- Public DataFlowInfo util functions -------------------------------

    data class InfoForBooleanOperator(
        val conditionalFromLeft: Collection<LogicStatement>,
        val conditionalFromRight: Collection<LogicStatement>,
        val knownFromRight: KnownInfos
    )

    abstract fun collectInfoForBooleanOperator(
        leftFlow: FLOW,
        leftVariable: DataFlowVariable,
        rightFlow: FLOW,
        rightVariable: DataFlowVariable
    ): InfoForBooleanOperator

    abstract fun approvePredicate(destination: MutableKnownFacts, predicate: Predicate, flow: FLOW)

    abstract fun approvePredicate(destination: MutableKnownFacts, predicate: Predicate, notApprovedFacts: Collection<LogicStatement>)

    abstract fun approvePredicate(predicate: Predicate, statements: Collection<LogicStatement>): MutableKnownFacts

    /**
     * Recursively collects all DataFlowInfos approved by [predicate] and all predicates
     *   that has been implied by it
     *   TODO: or not recursively?
     */
    abstract fun approvePredicate(flow: FLOW, predicate: Predicate): List<DataFlowInfo>

    fun orForVerifiedFacts(
        left: KnownInfos,
        right: KnownInfos
    ): MutableKnownFacts {
        if (left.isNullOrEmpty() || right.isNullOrEmpty()) return mutableMapOf()
        val map = mutableMapOf<RealVariable, MutableDataFlowInfo>()
        for (variable in left.keys.intersect(right.keys)) {
            val leftInfo = left.getValue(variable)
            val rightInfo = right.getValue(variable)
            map[variable] = or(listOf(leftInfo, rightInfo))
        }
        return map
    }

    // ------------------------------- Util functions -------------------------------

    // TODO
    protected fun <E> Collection<Collection<E>>.intersectSets(): Set<E> {
        if (isEmpty()) return emptySet()
        val iterator = iterator()
        val result = HashSet<E>(iterator.next())
        while (iterator.hasNext()) {
            result.retainAll(iterator.next())
        }
        return result
    }

    protected fun or(infos: Collection<DataFlowInfo>): MutableDataFlowInfo {
        require(infos.isNotEmpty())
        infos.singleOrNull()?.let { return it as MutableDataFlowInfo }
        val variable = infos.first().variable
        assert(infos.all { it.variable == variable })
        val exactType = orTypes(infos.map { it.exactType })
        val exactNotType = orTypes(infos.map { it.exactNotType })
        return MutableDataFlowInfo(variable, exactType, exactNotType)
    }

    private fun orTypes(types: Collection<Set<ConeKotlinType>>): MutableSet<ConeKotlinType> {
        if (types.any { it.isEmpty() }) return mutableSetOf()
        val allTypes = types.flatMapTo(mutableSetOf()) { it }
        val commonTypes = allTypes.toMutableSet()
        types.forEach { commonTypes.retainAll(it) }
        val differentTypes = allTypes - commonTypes
        context.commonSuperTypeOrNull(differentTypes.toList())?.let { commonTypes += it }
        return commonTypes
    }
}

/*
 *  used for:
 *   1. val b = x is String
 *   2. b = x is String
 *   3. !b | b.not()   for Booleans
 */
fun <F : Flow> LogicSystem<F>.replaceConditionalVariableInStatements(
    flow: F,
    originalVariable: DataFlowVariable,
    newVariable: DataFlowVariable,
    filter: (LogicStatement) -> Boolean = { true },
    transform: (LogicStatement) -> LogicStatement = { it }
) {
    translateConditionalVariableInStatements(
        flow,
        originalVariable,
        newVariable,
        shouldRemoveOriginalStatements = true,
        filter,
        transform
    )
}