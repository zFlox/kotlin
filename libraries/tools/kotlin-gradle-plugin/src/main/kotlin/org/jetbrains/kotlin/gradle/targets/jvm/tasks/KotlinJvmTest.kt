/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.jvm.tasks

import org.gradle.api.internal.tasks.testing.*
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.testing.Test

open class KotlinJvmTest : Test() {
    @Input
    @Optional
    var targetName: String? = null

    override fun createTestExecuter(): TestExecuter<JvmTestExecutionSpec> =
        if (targetName != null) Executor(
            super.createTestExecuter(),
            targetName!!
        )
        else super.createTestExecuter()

    class Executor(
        private val delegate: TestExecuter<JvmTestExecutionSpec>,
        private val targetName: String
    ) : TestExecuter<JvmTestExecutionSpec> by delegate {
        override fun execute(testExecutionSpec: JvmTestExecutionSpec, testResultProcessor: TestResultProcessor) {
            delegate.execute(testExecutionSpec, object : TestResultProcessor by testResultProcessor {
                override fun started(test: TestDescriptorInternal, event: TestStartEvent) {
                    testResultProcessor.started(
                        if (test is DefaultTestDescriptor) KotlinTestDescriptorWrapper(test) else test,
                        event
                    )
                }
            })
        }

        inner class KotlinTestDescriptorWrapper(private val delegate: DefaultTestDescriptor) : TestDescriptorInternal by delegate {
            override fun getName(): String = displayName
            override fun getDisplayName(): String = "${delegate.displayName}[$targetName]"
            override fun getClassName(): String? = delegate.className?.replace('$', '.')
            override fun getClassDisplayName(): String? = delegate.classDisplayName?.replace('$', '.')
        }
    }
}