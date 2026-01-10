package com.example.demo.kotlin.mvvm.hilt

import com.example.demo.CleanArchitectureConfig
import com.example.demo.helpers.DiStrategyRegistry
import com.example.demo.helpers.createFileIfNotExists
import com.example.demo.kotlin.mvvm.core.CommonCoreTemplates
import com.example.demo.kotlin.mvvm.core.DiStrategy
import com.intellij.psi.PsiDirectory

object HiltDiStrategy : DiStrategy {


    override val id = CleanArchitectureConfig.DependencyInjection.HILT
    override val language = CleanArchitectureConfig.Language.KOTLIN

    init { DiStrategyRegistry.register(this) }

    override fun ensureCoreDiFiles(
        coreDir: PsiDirectory,
        utilsDir: PsiDirectory,
        databaseDir: PsiDirectory
    ) {
        val commonDir = coreDir.findSubdirectory("common") ?: coreDir.createSubdirectory("common")
        val diDir = coreDir.findSubdirectory("di") ?: coreDir.createSubdirectory("di")

        // Common
        commonDir.createFileIfNotExists("Resource.kt", CommonCoreTemplates.resource)
        commonDir.createFileIfNotExists("DispatcherProvider.kt", CommonCoreTemplates.dispatcherProvider)
        commonDir.createFileIfNotExists("BaseViewModel.kt", CommonCoreTemplates.baseViewModel)
        commonDir.createFileIfNotExists("BaseFragment.kt", CommonCoreTemplates.baseFragment)
        commonDir.createFileIfNotExists("ErrorHandler.kt", CommonCoreTemplates.errorHandler)

        // Utils
        utilsDir.createFileIfNotExists("Constants.kt", CommonCoreTemplates.constants)

        // Hilt DI
        diDir.createFileIfNotExists("NetworkModule.kt", HiltDiTemplates.networkModule)
        diDir.createFileIfNotExists("DatabaseModule.kt", HiltDiTemplates.databaseModule)
        diDir.createFileIfNotExists("DispatcherModule.kt", HiltDiTemplates.dispatcherModule)

        // Database
        databaseDir.createFileIfNotExists("AppDatabase.kt", CommonCoreTemplates.appDatabase)
    }

    override fun createFeatureDiModule(
        diDir: PsiDirectory,
        featurePascal: String
    ) {
        diDir.createFileIfNotExists("${featurePascal}Module.kt", HiltDiTemplates.featureModule(featurePascal))
    }
}

