/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.gradle

import org.jetbrains.plugins.gradle.model.ClassSetProjectImportExtraModelProvider
import org.jetbrains.plugins.gradle.model.ProjectImportAction

val SUPPORTED_GRADLE_VERSIONS: Array<Array<Any>> = arrayOf<Array<Any>>(
    arrayOf("4.9"),
    arrayOf("5.6.4"),
    arrayOf("6.0.1")
)

// todo: inline when 183 becomes unsupported
fun addExtraProjectModelClasses(action: ProjectImportAction, classes: MutableSet<Class<*>>) {
    action.addProjectImportExtraModelProvider(ClassSetProjectImportExtraModelProvider(classes))
}