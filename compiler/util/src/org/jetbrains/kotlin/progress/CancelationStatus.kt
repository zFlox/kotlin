/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.progress

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicatorProvider

class CompilationCanceledException : ProcessCanceledException()

interface CompilationCanceledStatus {
    fun checkCanceled()
}

object ProgressIndicatorAndCompilationCanceledStatus {

    private var canceledStatus: CompilationCanceledStatus? = null

    @JvmStatic
    @Synchronized
    fun setCompilationCanceledStatus(newCanceledStatus: CompilationCanceledStatus?) {
        canceledStatus = newCanceledStatus
    }

    @JvmStatic
    fun checkCanceled() {
        ProgressIndicatorProvider.checkCanceled()
        canceledStatus?.checkCanceled()
    }


}

private data class CanceledStatusRecord(var enabled: Boolean = false, var timestamp: Long = System.currentTimeMillis())

class CompilationCanceledStatusHelper {
    companion object {
        @JvmStatic
        private val statusRecord: ThreadLocal<CanceledStatusRecord> = ThreadLocal.withInitial { CanceledStatusRecord() }

        @JvmStatic
        private val threshold: Long = System.getProperty("idea.CheckCancelThreshold", "1000").toLong()

        @JvmStatic
        fun isEnabled() = statusRecord.get().enabled

        @JvmStatic
        fun checkCancellationDiff() {
            val record = statusRecord.get()
            if (record.enabled) {
                val now = System.currentTimeMillis()
                val diff = now - record.timestamp
                if (diff > threshold) {
                    try {
                        throw RuntimeException("${Thread.currentThread().name} checkCanceled: $diff ms")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                record.timestamp = now
            }
        }

        @JvmStatic
        internal fun enableCancellationTimer(enabled: Boolean) {
            val record = statusRecord.get()
            record.enabled = enabled
            record.timestamp = System.currentTimeMillis()
        }

    }
}

fun <T> runWithCheckCancellation(block: () -> T): T {
    // checkCancel check is already enabled
    if (CompilationCanceledStatusHelper.isEnabled()) return block()

    CompilationCanceledStatusHelper.enableCancellationTimer(true)
    try {
        return block()
    } finally {
        CompilationCanceledStatusHelper.checkCancellationDiff()
        CompilationCanceledStatusHelper.enableCancellationTimer(false)
    }
}
