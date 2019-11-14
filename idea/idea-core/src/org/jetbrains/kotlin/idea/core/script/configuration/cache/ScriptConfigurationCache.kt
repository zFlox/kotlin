/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.configuration.cache

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.NonClasspathDirectoriesScope
import org.jetbrains.kotlin.idea.caches.project.getAllProjectSdks
import org.jetbrains.kotlin.idea.core.script.ScriptConfigurationManager
import org.jetbrains.kotlin.idea.core.script.configuration.utils.getKtFile
import org.jetbrains.kotlin.idea.util.application.runReadAction
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.scripting.resolve.ScriptCompilationConfigurationWrapper
import kotlin.script.experimental.api.ScriptDiagnostic

/**
 * Cached configurations for the file's specific snapshot state.
 *
 * The writer should put related inputs snapshot for loaded configuration.
 * This would allow making up-to-date checks for existed entry.
 *
 * The configuration may be loaded but not applied. So, it makes
 * sense to do up-to-date check on loaded configuration (not on applied).
 * For those reasons, we are storing both for each file.
 */
interface ScriptConfigurationCache {
    operator fun get(file: VirtualFile): ScriptConfigurationState?

    fun setApplied(file: VirtualFile, configurationSnapshot: ScriptConfigurationSnapshot)
    fun setLoaded(file: VirtualFile, configurationSnapshot: ScriptConfigurationSnapshot)
    fun markOutOfDate(file: VirtualFile)

    fun allApplied(): Collection<ScriptConfigurationSnapshot>
    fun clear()
}

data class ScriptConfigurationState(
    val applied: ScriptConfigurationSnapshot? = null,
    val loaded: ScriptConfigurationSnapshot? = null
) {
    fun isUpToDate(project: Project, file: VirtualFile, ktFile: KtFile? = null): Boolean =
        (loaded ?: applied)?.inputs?.isUpToDate(project, file, ktFile) ?: false
}

data class ScriptConfigurationSnapshot(
    val inputs: CachedConfigurationInputs,
    val reports: List<ScriptDiagnostic>,
    val configuration: ScriptCompilationConfigurationWrapper?
) {
    val sdk: Sdk? by lazy {
        // workaround for mismatched gradle wrapper and plugin version
        val javaHome = try {
            configuration?.javaHome?.let { VfsUtil.findFileByIoFile(it, true) }
        } catch (e: Throwable) {
            null
        } ?: return@lazy null

        getAllProjectSdks().find { it.homeDirectory == javaHome }
    }

    val scope: GlobalSearchScope by lazy {
        if (configuration == null) GlobalSearchScope.EMPTY_SCOPE
        else {
            val roots = configuration.dependenciesClassPath
            val sdk = sdk

            if (sdk == null) {
                NonClasspathDirectoriesScope.compose(ScriptConfigurationManager.toVfsRoots(roots))
            } else {
                NonClasspathDirectoriesScope.compose(
                    sdk.rootProvider.getFiles(OrderRootType.CLASSES).toList() +
                            ScriptConfigurationManager.toVfsRoots(roots)
                )
            }
        }
    }
}

interface CachedConfigurationInputs {
    fun isUpToDate(project: Project, file: VirtualFile, ktFile: KtFile? = null): Boolean

    object OutOfDate : CachedConfigurationInputs {
        override fun isUpToDate(project: Project, file: VirtualFile, ktFile: KtFile?): Boolean = false
    }

    data class PsiModificationStamp(
        val fileModificationStamp: Long,
        val psiModificationStamp: Long
    ) : CachedConfigurationInputs {
        override fun isUpToDate(project: Project, file: VirtualFile, ktFile: KtFile?): Boolean =
            get(project, file, ktFile) == this

        companion object {
            fun get(project: Project, file: VirtualFile, ktFile: KtFile?): PsiModificationStamp {
                val actualKtFile = project.getKtFile(file, ktFile)
                return PsiModificationStamp(
                    file.modificationStamp,
                    actualKtFile?.modificationStamp ?: 0
                )
            }
        }
    }

    data class SourceContentsStamp(val source: String) : CachedConfigurationInputs {
        override fun isUpToDate(project: Project, file: VirtualFile, ktFile: KtFile?): Boolean =
            get(project, file, ktFile) == this

        companion object {
            fun get(project: Project, file: VirtualFile, ktFile: KtFile?): SourceContentsStamp {
                val text = runReadAction {
                    FileDocumentManager.getInstance().getDocument(file)!!.text
                }

                return SourceContentsStamp(text)
            }
        }
    }
}