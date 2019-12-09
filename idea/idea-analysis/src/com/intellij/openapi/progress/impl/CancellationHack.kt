/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.intellij.openapi.progress.impl

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import org.jetbrains.kotlin.progress.CompilationCanceledStatusHelper

class CancellationHack : ProjectComponent {
    // TODO: IT REQUIRES public interface com.intellij.openapi.progress.impl.CoreProgressManager.CheckCanceledHook

    private val enabled = "enabled" == System.getProperty("idea.CheckCancelCheck")

    override fun initComponent() {
        if (!enabled) return

        check(!CoreProgressManager.ENABLED) {
            "-Didea.CheckCancelCheck=enabled has to be run together with -Didea.ProcessCanceledException=disabled to enable hook-only mode"
        }

        val progressManagerImpl = ProgressManager.getInstance() as ProgressManagerImpl

        // same as `progressManagerImpl.addCheckCanceledHook(hook)` but via reflection due to visibility limitations of plugin
        val method =
            progressManagerImpl::class.java.getDeclaredMethod("addCheckCanceledHook", CoreProgressManager.CheckCanceledHook::class.java)
        method.isAccessible = true
        method.invoke(progressManagerImpl, hook)
    }

    override fun disposeComponent() {
        if (!enabled) return

        val progressManagerImpl = ProgressManager.getInstance() as ProgressManagerImpl

        // same as `progressManagerImpl.removeCheckCanceledHook(hook)` but via reflection due to visibility limitations of plugin
        val method =
            progressManagerImpl::class.java.getDeclaredMethod("removeCheckCanceledHook", CoreProgressManager.CheckCanceledHook::class.java)
        method.isAccessible = true
        method.invoke(progressManagerImpl, hook)
    }

    companion object {
        @JvmStatic
        private val localProgressIndicator: ThreadLocal<MutableSet<ProgressIndicator>> =
            ThreadLocal.withInitial { mutableSetOf<ProgressIndicator>() }

        private val hook = CoreProgressManager.CheckCanceledHook { indicator ->
            indicator?.let {
                if (!it.isCanceled) {
                    val set = localProgressIndicator.get()
                    if (set.add(it)) {
                        try {
                            CompilationCanceledStatusHelper.checkCancellationDiff()
                            it.checkCanceled()
                        } finally {
                            set.remove(it)
                        }
                    }
                }
            }
            indicator != null
        }
    }



}
