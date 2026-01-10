package com.example.demo.flutter.mvvm.bloc

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.core.FlutterMvvmStrategy
import com.example.demo.helpers.NameUtils.toPascalCase
import com.example.demo.helpers.NameUtils.toSnakeCase
import com.example.demo.helpers.createFileIfNotExists
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiManager

object BlocFlutterStrategy : FlutterMvvmStrategy {

    override val supportedDi = setOf(
        CleanArchitectureConfig.DependencyInjection.GET_IT,
        CleanArchitectureConfig.DependencyInjection.NONE,
    )

    override val stateManagement =
        CleanArchitectureConfig.StateManagement.BLOC

    override fun generateFeature(project: Project, config: CleanArchitectureConfig) {
        val baseDir = project.baseDir ?: return
        val libDir = VfsUtil.findRelativeFile(baseDir, "lib") ?: return
        val psiManager = PsiManager.getInstance(project)
        val psiLibDir = psiManager.findDirectory(libDir) ?: return

        val featurePascal = config.className.toPascalCase()
        val featureSnake = config.className.toSnakeCase()

        val featuresDir = psiLibDir.findSubdirectory("features")
            ?: psiLibDir.createSubdirectory("features")
        val featureDir = featuresDir.findSubdirectory(featureSnake)
            ?: featuresDir.createSubdirectory(featureSnake)

        val dataDir = featureDir.findSubdirectory("data") ?: featureDir.createSubdirectory("data")
        val domainDir = featureDir.findSubdirectory("domain") ?: featureDir.createSubdirectory("domain")
        val presentationDir = featureDir.findSubdirectory("presentation") ?: featureDir.createSubdirectory("presentation")

        val modelDir = domainDir.findSubdirectory("model") ?: domainDir.createSubdirectory("model")
        val repoDir = domainDir.findSubdirectory("repository") ?: domainDir.createSubdirectory("repository")
        val useCaseDir = domainDir.findSubdirectory("usecase") ?: domainDir.createSubdirectory("usecase")

        val remoteDir = dataDir.findSubdirectory("remote") ?: dataDir.createSubdirectory("remote")
        val repoImplDir = dataDir.findSubdirectory("repository") ?: dataDir.createSubdirectory("repository")

        val uiDir = presentationDir.findSubdirectory("ui") ?: presentationDir.createSubdirectory("ui")
        val vmDir = presentationDir.findSubdirectory("viewmodel") ?: presentationDir.createSubdirectory("viewmodel")

        modelDir.createFileIfNotExists(
            "${featureSnake}_model.dart",
            BlocTemplates.domainModel(featurePascal, featureSnake)
        )

        repoDir.createFileIfNotExists(
            "${featureSnake}_repository.dart",
            BlocTemplates.domainRepository(featurePascal, featureSnake)
        )

        useCaseDir.createFileIfNotExists(
            "get_${featureSnake}_list_usecase.dart",
            BlocTemplates.useCase(featurePascal, featureSnake)
        )

        remoteDir.createFileIfNotExists(
            "${featureSnake}_api_service.dart",
            BlocTemplates.apiService(featurePascal, featureSnake)
        )

        repoImplDir.createFileIfNotExists(
            "${featureSnake}_repository_impl.dart",
            BlocTemplates.repositoryImpl(featurePascal, featureSnake)
        )

        vmDir.createFileIfNotExists(
            "${featureSnake}_event.dart",
            BlocTemplates.event(featurePascal)
        )

        vmDir.createFileIfNotExists(
            "${featureSnake}_state.dart",
            BlocTemplates.state(featurePascal, featureSnake)
        )

        vmDir.createFileIfNotExists(
            "${featureSnake}_bloc.dart",
            BlocTemplates.bloc(featurePascal, featureSnake)
        )

        uiDir.createFileIfNotExists(
            "${featureSnake}_screen.dart",
            BlocTemplates.screen(featurePascal, featureSnake, config.di)
        )
    }
}
