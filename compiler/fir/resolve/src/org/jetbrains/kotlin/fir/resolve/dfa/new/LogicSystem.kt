/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.dfa.new

import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.UniversalConeInferenceContext
import org.jetbrains.kotlin.fir.types.ConeKotlinType

interface Flow {
    fun getKnownInfo(variable: RealVariable): DataFlowInfo?
    fun getDfaConditions(variable: DataFlowVariable): Collection<LogicStatement>
    fun getVariablesInKnownInfos(): Collection<RealVariable>
    fun removeConditions(variable: DataFlowVariable): Collection<LogicStatement>
}

fun KnownInfos.intersect(other: KnownInfos): MutableKnownFacts = TODO()

abstract class LogicSystem(private val context: UniversalConeInferenceContext) {
    // ------------------------------- Flow operations -------------------------------

    abstract fun createEmptyFlow(): Flow
    abstract fun forkFlow(flow: Flow): Flow
    abstract fun joinFlow(flows: Collection<Flow>): Flow

    abstract fun addKnownInfo(flow: Flow, info: DataFlowInfo)

    abstract fun addLogicStatement(flow: Flow, statement: LogicStatement)

    abstract fun removeAllAboutVariable(flow: Flow, variable: RealVariable)

    /*
     *  used for:
     *   1. val b = x is String
     *   2. b = x is String
     *   3. !b | b.not()   for Booleans
     */
    fun replaceConditionalVariableInStatements(
        flow: Flow,
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

    abstract fun translateConditionalVariableInStatements(
        flow: Flow,
        originalVariable: DataFlowVariable,
        newVariable: DataFlowVariable,
        shouldRemoveOriginalStatements: Boolean,
        filter: (LogicStatement) -> Boolean = { true },
        transform: (LogicStatement) -> LogicStatement = { it }
    )

    abstract fun approveStatementsInsideFlow(
        flow: Flow,
        predicate: Predicate,
        shouldForkFlow: Boolean,
        shouldRemoveSynthetics: Boolean
    ): Flow

    // ------------------------------- Callbacks for updating implicit receiver stack -------------------------------

    abstract fun processUpdatedReceiverVariable(flow: Flow, variable: RealVariable)
    abstract fun updateAllReceivers(flow: Flow)

    // ------------------------------- Accessors to flow implementation -------------------------------

    protected abstract val Flow.knownFacts: MutableKnownFacts
    protected abstract val Flow.logicStatements: LogicStatements

    // ------------------------------- Public DataFlowInfo util functions -------------------------------

    data class InfoForBooleanOperator(
        val conditionalFromLeft: Collection<LogicStatement>,
        val conditionalFromRight: Collection<LogicStatement>,
        val knownFromRight: KnownInfos
    )

    abstract fun collectInfoForBooleanOperator(
        leftFlow: Flow,
        leftVariable: DataFlowVariable,
        rightFlow: Flow,
        rightVariable: DataFlowVariable
    ): InfoForBooleanOperator

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

    abstract fun approvePredicate(destination: MutableKnownFacts, predicate: Predicate, flow: Flow)

    abstract fun approvePredicate(destination: MutableKnownFacts, predicate: Predicate, notApprovedFacts: Collection<LogicStatement>)

    abstract fun approvePredicate(predicate: Predicate, statements: Collection<LogicStatement>): MutableKnownFacts

    /**
      * Recursively collects all DataFlowInfos approved by [predicate] and all predicates
      *   that has been implied by it
     *   TODO: or not recursively?
      */
    abstract fun approvePredicate(flow: Flow, predicate: Predicate): List<DataFlowInfo>

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