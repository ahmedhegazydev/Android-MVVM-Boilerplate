package com.example.demo.flutter.mvvm.provider

import com.example.demo.CleanArchitectureConfig
import com.example.demo.helpers.NameUtils.toCamelCase
import com.example.demo.helpers.NameUtils.toSnakeCase
import com.example.demo.helpers.NameUtils.toPascalCase
import com.example.demo.flutter.mvvm.core.FlutterMvvmStrategy
import com.example.demo.flutter.mvvm.core.FlutterStrategyRegistry
import com.example.demo.helpers.createFileIfNotExists
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiManager

object ProviderFlutterStrategy : FlutterMvvmStrategy {


    init {
        FlutterStrategyRegistry.register(this)
    }

    override val supportedDi = setOf(
        CleanArchitectureConfig.DependencyInjection.GET_IT,
        CleanArchitectureConfig.DependencyInjection.NONE,
    )

    override val stateManagement =
        CleanArchitectureConfig.StateManagement.PROVIDER

    override fun generateFeature(project: Project, config: CleanArchitectureConfig) {
        val baseDir = project.baseDir ?: return
        val libDir = VfsUtil.findRelativeFile(baseDir, "lib") ?: return
        val psiManager = PsiManager.getInstance(project)
        val psiLibDir = psiManager.findDirectory(libDir) ?: return

        val featurePascal = config.className.toPascalCase()   // FooBar
        val featureSnake = config.className.toSnakeCase()     // foo_bar
        val featureCamel = config.className.toCamelCase()     // fooBar

        // lib/features/<feature_snake>/
        val featuresDir = psiLibDir.findSubdirectory("features")
            ?: psiLibDir.createSubdirectory("features")
        val featureDir = featuresDir.findSubdirectory(featureSnake)
            ?: featuresDir.createSubdirectory(featureSnake)

        val dataDir = featureDir.findSubdirectory("data") ?: featureDir.createSubdirectory("data")
        val domainDir = featureDir.findSubdirectory("domain") ?: featureDir.createSubdirectory("domain")
        val presentationDir =
            featureDir.findSubdirectory("presentation") ?: featureDir.createSubdirectory("presentation")

        val modelDir = domainDir.findSubdirectory("model") ?: domainDir.createSubdirectory("model")
        val useCaseDir = domainDir.findSubdirectory("usecase") ?: domainDir.createSubdirectory("usecase")
        val repoDir = domainDir.findSubdirectory("repository") ?: domainDir.createSubdirectory("repository")

        val remoteDir = dataDir.findSubdirectory("remote") ?: dataDir.createSubdirectory("remote")
        val repoImplDir = dataDir.findSubdirectory("repository") ?: dataDir.createSubdirectory("repository")

        val uiDir = presentationDir.findSubdirectory("ui") ?: presentationDir.createSubdirectory("ui")
        val vmDir = presentationDir.findSubdirectory("viewmodel") ?: presentationDir.createSubdirectory("viewmodel")

        // ===== Dart files (as Strings) =====
        modelDir.createFileIfNotExists(
            "${featureSnake}_model.dart",
            ProviderTemplates.domainModel(featurePascal, featureSnake)
        )

        repoDir.createFileIfNotExists(
            "${featureSnake}_repository.dart",
            ProviderTemplates.domainRepository(featurePascal, featureSnake)
        )

        useCaseDir.createFileIfNotExists(
            "get_${featureSnake}_list_usecase.dart",
            ProviderTemplates.useCase(featurePascal, featureSnake)
        )

        remoteDir.createFileIfNotExists(
            "${featureSnake}_api_service.dart",
            ProviderTemplates.apiService(featurePascal, featureSnake)
        )

        repoImplDir.createFileIfNotExists(
            "${featureSnake}_repository_impl.dart",
            ProviderTemplates.repositoryImpl(featurePascal, featureSnake)
        )

        vmDir.createFileIfNotExists(
            "${featureSnake}_view_model.dart",
            ProviderTemplates.viewModel(featurePascal, featureSnake)
        )

        uiDir.createFileIfNotExists(
            "${featureSnake}_screen.dart",
            ProviderTemplates.screen(featurePascal, featureSnake, featureCamel, config.di)
        )

    }
}

