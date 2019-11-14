/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.configuration.utils

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.NonClasspathDirectoriesScope
import org.jetbrains.kotlin.idea.core.script.ScriptConfigurationManager
import org.jetbrains.kotlin.idea.core.script.configuration.cache.ScriptConfigurationSnapshot
import org.jetbrains.kotlin.idea.core.script.debug
import org.jetbrains.kotlin.scripting.resolve.ScriptCompilationConfigurationWrapper

internal abstract class ScriptClassRootsCache(
    private val project: Project,
    private val allApplied: Collection<ScriptConfigurationSnapshot>
) {
    val allConfigurations get() = allApplied.mapNotNull { it.configuration }

    protected abstract fun getConfiguration(file: VirtualFile): ScriptCompilationConfigurationWrapper?

    val firstScriptSdk: Sdk? by lazy {
        allApplied.firstOrNull { it.configuration != null }?.sdk
    }

    private val allSdks: Set<Sdk> by lazy {
        allApplied.mapNotNull { it.sdk }.toSet()
    }

    private val allNonIndexedSdks by lazy {
        allApplied.mapNotNull { it.sdk }
            .filterNonModuleSdk()
            .distinct()
    }

    private fun List<Sdk>.filterNonModuleSdk(): List<Sdk> {
        val moduleSdks = ModuleManager.getInstance(project).modules.map { ModuleRootManager.getInstance(it).sdk }
        return filterNot { moduleSdks.contains(it) }
    }

    val allDependenciesClassFiles by lazy {
        val sdkFiles = allNonIndexedSdks.flatMap { it.rootProvider.getFiles(OrderRootType.CLASSES).toList() }
        val scriptDependenciesClasspath = allConfigurations.flatMap { it.dependenciesClassPath }.distinct()

        (sdkFiles + ScriptConfigurationManager.toVfsRoots(scriptDependenciesClasspath)).toSet()
    }

    val allDependenciesSources by lazy {
        val sdkSources = allNonIndexedSdks.flatMap { it.rootProvider.getFiles(OrderRootType.SOURCES).toList() }
        val scriptDependenciesSources = allConfigurations.flatMap { it.dependenciesSources }.distinct()

        (sdkSources + ScriptConfigurationManager.toVfsRoots(scriptDependenciesSources)).toSet()
    }

    val allDependenciesClassFilesScope by lazy {
        NonClasspathDirectoriesScope.compose(allDependenciesClassFiles.toList())
    }

    val allDependenciesSourcesScope by lazy {
        NonClasspathDirectoriesScope.compose(allDependenciesSources.toList())
    }

    fun hasNotCachedRoots(configurationSnapshot: ScriptConfigurationSnapshot): Boolean {
        val configuration = configurationSnapshot.configuration ?: return false
        val scriptSdk = configurationSnapshot.sdk ?: ScriptConfigurationManager.getScriptDefaultSdk(project)

        val wasSdkChanged = scriptSdk != null && !allSdks.contains(scriptSdk)
        if (wasSdkChanged) {
            debug { "sdk was changed: $configuration" }
            return true
        }

        val newClassRoots = ScriptConfigurationManager.toVfsRoots(configuration.dependenciesClassPath)
        for (newClassRoot in newClassRoots) {
            if (!allDependenciesClassFiles.contains(newClassRoot)) {
                debug { "class root was changed: $newClassRoot" }
                return true
            }
        }

        val newSourceRoots = ScriptConfigurationManager.toVfsRoots(configuration.dependenciesSources)
        for (newSourceRoot in newSourceRoots) {
            if (!allDependenciesSources.contains(newSourceRoot)) {
                debug { "source root was changed: $newSourceRoot" }
                return true
            }
        }

        return false
    }
}