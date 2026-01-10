package com.example.demo.java.mvvm.dagger

import com.example.demo.CleanArchitectureConfig
import com.example.demo.helpers.DiStrategyRegistry
import com.example.demo.helpers.createFileIfNotExists
import com.example.demo.java.mvvm.core.JavaCoreTemplates
import com.example.demo.kotlin.mvvm.core.DiStrategy
import com.intellij.psi.PsiDirectory

object DaggerDiStrategy : DiStrategy {


    override val id = CleanArchitectureConfig.DependencyInjection.DAGGER
    override val language = CleanArchitectureConfig.Language.JAVA

    init { DiStrategyRegistry.register(this) }



    override fun ensureCoreDiFiles(
        coreDir: PsiDirectory,
        utilsDir: PsiDirectory,
        databaseDir: PsiDirectory
    ) {
        val commonDir = coreDir.findSubdirectory("common") ?: coreDir.createSubdirectory("common")
        val diDir = coreDir.findSubdirectory("di") ?: coreDir.createSubdirectory("di")

        // Common (Java)
        commonDir.createFileIfNotExists("Resource.java", JavaCoreTemplates.resource)
        commonDir.createFileIfNotExists("DispatcherProvider.java", JavaCoreTemplates.dispatcherProvider)
        commonDir.createFileIfNotExists("BaseViewModel.java", JavaCoreTemplates.baseViewModel)
        commonDir.createFileIfNotExists("BaseFragment.java", JavaCoreTemplates.baseFragment)
        commonDir.createFileIfNotExists("ErrorHandler.java", JavaCoreTemplates.errorHandler)

        // Utils
        utilsDir.createFileIfNotExists("Constants.java", JavaCoreTemplates.constants)

        // Dagger DI
        diDir.createFileIfNotExists("NetworkModule.java", JavaDaggerTemplates.networkModule)
        diDir.createFileIfNotExists("DatabaseModule.java", JavaDaggerTemplates.databaseModule)
        diDir.createFileIfNotExists("DispatcherModule.java", JavaDaggerTemplates.dispatcherModule)

        // Database
        databaseDir.createFileIfNotExists("AppDatabase.java", JavaCoreTemplates.appDatabase)
    }

    override fun createFeatureDiModule(
        diDir: PsiDirectory,
        featurePascal: String
    ) {
        diDir.createFileIfNotExists("${featurePascal}Module.java", JavaDaggerTemplates.featureModule(featurePascal))
    }
}

