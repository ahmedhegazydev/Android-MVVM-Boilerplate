package com.example.demo.kotlin.mvvm.core

import com.example.demo.CleanArchitectureConfig
import com.intellij.psi.PsiDirectory

interface DiStrategy {

    val id: CleanArchitectureConfig.DependencyInjection
    val language: CleanArchitectureConfig.Language

    fun ensureCoreDiFiles(coreDir: PsiDirectory, utilsDir: PsiDirectory, databaseDir: PsiDirectory)
    fun createFeatureDiModule(diDir: PsiDirectory, featurePascal: String)

}
