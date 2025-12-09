package com.example.demo.kotlin.mvvm.core

import com.example.demo.CleanArchitectureConfig
import com.intellij.psi.PsiDirectory

interface DiStrategy {
    val id: CleanArchitectureConfig.DependencyInjection

    /** Core DI files (Network, Database, Dispatcher, …) */
    fun ensureCoreDiFiles(coreDir: PsiDirectory, utilsDir: PsiDirectory, databaseDir: PsiDirectory)

    /** Feature-level DI (Module / Koin module) */
    fun createFeatureDiModule(
        diDir: PsiDirectory,
        featurePascal: String
    )
}
