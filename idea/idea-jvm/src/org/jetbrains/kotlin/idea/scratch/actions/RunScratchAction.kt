/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scratch.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.DumbService
import com.intellij.task.ProjectTaskManager
import org.jetbrains.kotlin.idea.KotlinBundle
import org.jetbrains.kotlin.idea.scratch.ScratchFile
import org.jetbrains.kotlin.idea.scratch.SequentialScratchExecutor
import org.jetbrains.kotlin.idea.scratch.getScratchFileFromSelectedEditor
import org.jetbrains.kotlin.idea.scratch.printDebugMessage
import org.jetbrains.kotlin.idea.scratch.LOG as log

class RunScratchAction : ScratchAction(
    KotlinBundle.message("scratch.run.button"),
    AllIcons.Actions.Execute
) {

    init {
        shortcutSet = CustomShortcutSet.fromString("control alt W")
        templatePresentation.text += " (${KeymapUtil.getShortcutText(shortcutSet.shortcuts.first())})"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val scratchFile = getScratchFileFromSelectedEditor(project) ?: return

        doAction(scratchFile, false)
    }

    companion object {
        fun doAction(scratchFile: ScratchFile, isAutoRun: Boolean) {
            val isRepl = scratchFile.options.isRepl
            val executor = (if (isRepl) scratchFile.replScratchExecutor else scratchFile.compilingScratchExecutor) ?: return

            log.printDebugMessage("Run Action: isRepl = $isRepl")

            fun executeScratch() {
                try {
                    if (isAutoRun && executor is SequentialScratchExecutor) {
                        executor.executeNew()
                    } else {
                        executor.execute()
                    }
                } catch (ex: Throwable) {
                    executor.errorOccurs("Exception occurs during Run Scratch Action", ex, true)
                }
            }

            val isMakeBeforeRun = scratchFile.options.isMakeBeforeRun
            log.printDebugMessage("Run Action: isMakeBeforeRun = $isMakeBeforeRun")

            val module = scratchFile.module
            log.printDebugMessage("Run Action: module = ${module?.name}")

            if (!isAutoRun && module != null && isMakeBeforeRun) {
                val project = scratchFile.project
                ProjectTaskManager.getInstance(project).build(arrayOf(module)) { result ->
                    if (result.isAborted || result.errors > 0) {
                        executor.errorOccurs("There were compilation errors in module ${module.name}")
                    }

                    if (DumbService.isDumb(project)) {
                        DumbService.getInstance(project).smartInvokeLater {
                            executeScratch()
                        }
                    } else {
                        executeScratch()
                    }
                }
            } else {
                executeScratch()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = !ScratchCompilationSupport.isAnyInProgress()

        if (e.presentation.isEnabled) {
            e.presentation.text = templatePresentation.text
        } else {
            e.presentation.text = "Other Scratch file execution is in progress"
        }

        val project = e.project ?: return
        val scratchFile = getScratchFileFromSelectedEditor(project) ?: return

        e.presentation.isVisible = !ScratchCompilationSupport.isInProgress(scratchFile)
    }
}