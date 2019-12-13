/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scripting.gradle

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.PathUtil
import org.jetbrains.annotations.TestOnly
import java.nio.file.Paths
import java.util.*

private val comparator = Comparator<VirtualFile> { f1, f2 -> (f1.timeStamp - f2.timeStamp).toInt() }

@State(
    name = "KotlinGradleScriptsModificationInfo",
    storages = [Storage(StoragePathMacros.CACHE_FILE)]
)
class GradleScriptInputsWatcher(val project: Project) : PersistentStateComponent<GradleScriptInputsWatcher.State> {
    companion object {
        fun getInstance(project: Project): GradleScriptInputsWatcher {
            return ServiceManager.getService(project, GradleScriptInputsWatcher::class.java)
        }
    }

    fun startWatching() {
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    if (project.isDisposed) return

                    val files = getAffectedGradleProjectFiles(project)
                    for (event in events) {
                        val file = event.file ?: return
                        if (isInAffectedGradleProjectFiles(files, file)) {
                            addToStorage(file)
                        }
                    }
                }
            })
    }

    fun lastModifiedFileTimeStamp(file: VirtualFile): Long = lastModifiedRelatedFile(file)?.timeStamp ?: 0

    fun areRelatedFilesUpToDate(file: VirtualFile, timeStamp: Long): Boolean {
        return lastModifiedFileTimeStamp(file) <= timeStamp
    }

    private val storage = TreeSet(comparator)

    private fun addToStorage(file: VirtualFile) {
        if (storage.contains(file)) {
            storage.remove(file)
        }
        storage.add(file)

        state.storage = storage.toList().mapNotNull { state.getNormalizedPath(it) }
    }

    private fun lastModifiedRelatedFile(file: VirtualFile): VirtualFile? {
        if (storage.isEmpty()) return null

        val iterator = storage.descendingIterator()
        if (!iterator.hasNext()) return null

        var lastModifiedFile = iterator.next()
        while (lastModifiedFile == file && iterator.hasNext()) {
            lastModifiedFile = iterator.next()
        }

        if (lastModifiedFile == file) return null

        return lastModifiedFile
    }

    private var state: State = State()

    class State {
        var storage: List<String> = arrayListOf()
        fun getNormalizedPath(file: VirtualFile): String? {
            val canonized = PathUtil.getCanonicalPath(file.path)
            return if (canonized == null) null else FileUtil.toSystemIndependentName(canonized)
        }

    }

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state

        loadFiles()
    }

    private fun loadFiles() {
        val files = getAffectedGradleProjectFiles(project)
        state.storage.forEach {
            val file = VfsUtil.findFile(Paths.get(it), true)
            if (file != null && isInAffectedGradleProjectFiles(files, file)) {
                storage.add(file)
            }
        }
    }

    @TestOnly
    fun clearAndRefillState() {
        storage.clear()
        loadFiles()
    }
}