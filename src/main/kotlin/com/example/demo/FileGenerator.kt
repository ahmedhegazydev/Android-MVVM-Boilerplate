package com.example.demo

import com.example.demo.helpers.NameUtils.toCamelCase
import com.example.demo.helpers.NameUtils.toPascalCase
import com.example.demo.helpers.NameUtils.toSnakeCase
import com.example.demo.java.mvvm.dagger.DaggerDiStrategy
import com.example.demo.java.mvvm.dagger.JavaFeatureTemplates
import com.example.demo.kotlin.mvvm.core.DiStrategy
import com.example.demo.kotlin.mvvm.hilt.HiltDiStrategy
import com.example.demo.kotlin.mvvm.koin.KoinDiStrategy
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

object FileGenerator {

    private val diStrategies: List<DiStrategy> = listOf(
        HiltDiStrategy,   // Kotlin + Hilt
        KoinDiStrategy,   // Kotlin + Koin
        DaggerDiStrategy  // Java + Dagger
    )

    fun generate(project: Project, config: CleanArchitectureConfig) {
        val baseDir = project.baseDir ?: return
        val psiManager = PsiManager.getInstance(project)

        val srcRoot = VfsUtil.findRelativeFile(baseDir, "app", "src", "main", "java") ?: return
        val psiSrcRoot = psiManager.findDirectory(srcRoot) ?: return

        val diStrategy = diStrategies.firstOrNull { it.id == config.di } ?: return

        // 1) Core files
        ensureCoreFiles(psiSrcRoot, diStrategy, config.language)

        // 2) Feature
        generateFeature(project, psiSrcRoot, config, diStrategy)
    }

    private fun generateFeature(
        project: Project,
        psiSrcRoot: PsiDirectory,
        config: CleanArchitectureConfig,
        diStrategy: DiStrategy
    ) {
        val featurePascal = config.className.toPascalCase()
        val featureCamel = config.className.toCamelCase()
        val featureSnake = config.className.toSnakeCase()

        val featuresDir = psiSrcRoot.findSubdirectory("features") ?: psiSrcRoot.createSubdirectory("features")
        val featureDir = featuresDir.findSubdirectory(featureCamel) ?: featuresDir.createSubdirectory(featureCamel)

        val dataDir = featureDir.findSubdirectory("data") ?: featureDir.createSubdirectory("data")
        val domainDir = featureDir.findSubdirectory("domain") ?: featureDir.createSubdirectory("domain")
        val presentationDir =
            featureDir.findSubdirectory("presentation") ?: featureDir.createSubdirectory("presentation")
        val diDir = featureDir.findSubdirectory("di") ?: featureDir.createSubdirectory("di")

        val remoteDir = dataDir.findSubdirectory("remote") ?: dataDir.createSubdirectory("remote")
        val remoteDtoDir = remoteDir.findSubdirectory("dto") ?: remoteDir.createSubdirectory("dto")
        val localDir = dataDir.findSubdirectory("local") ?: dataDir.createSubdirectory("local")
        val entityDir = localDir.findSubdirectory("entity") ?: localDir.createSubdirectory("entity")
        val daoDir = localDir.findSubdirectory("dao") ?: localDir.createSubdirectory("dao")
        val repoImplDir = dataDir.findSubdirectory("repository") ?: dataDir.createSubdirectory("repository")

        val domainModelDir = domainDir.findSubdirectory("model") ?: domainDir.createSubdirectory("model")
        val domainRepoDir = domainDir.findSubdirectory("repository") ?: domainDir.createSubdirectory("repository")
        val useCaseDir = domainDir.findSubdirectory("usecase") ?: domainDir.createSubdirectory("usecase")

        val uiDir = presentationDir.findSubdirectory("ui") ?: presentationDir.createSubdirectory("ui")
        val viewModelDir =
            presentationDir.findSubdirectory("viewmodel") ?: presentationDir.createSubdirectory("viewmodel")
        val stateDir = presentationDir.findSubdirectory("state") ?: presentationDir.createSubdirectory("state")

        when (config.language) {
            CleanArchitectureConfig.Language.KOTLIN -> {
                // ===== Kotlin Domain =====
                domainModelDir.createFileIfNotExists(
                    "$featurePascal.kt",
                    FeatureTemplates.domainModel(featurePascal)
                )
                domainRepoDir.createFileIfNotExists(
                    "${featurePascal}Repository.kt",
                    FeatureTemplates.domainRepository(featurePascal)
                )
                useCaseDir.createFileIfNotExists(
                    "Get${featurePascal}ListUseCase.kt",
                    FeatureTemplates.useCase(featurePascal)
                )

                // ===== Kotlin Data =====
                remoteDtoDir.createFileIfNotExists(
                    "${featurePascal}Dto.kt",
                    FeatureTemplates.dto(featurePascal)
                )
                remoteDir.createFileIfNotExists(
                    "${featurePascal}ApiService.kt",
                    FeatureTemplates.apiService(featurePascal)
                )
                entityDir.createFileIfNotExists(
                    "${featurePascal}Entity.kt",
                    FeatureTemplates.entity(featurePascal)
                )
                daoDir.createFileIfNotExists(
                    "${featurePascal}Dao.kt",
                    FeatureTemplates.dao(featurePascal)
                )
                repoImplDir.createFileIfNotExists(
                    "${featurePascal}RepositoryImpl.kt",
                    FeatureTemplates.repositoryImpl(featurePascal)
                )

                // ===== Kotlin Presentation =====
                stateDir.createFileIfNotExists(
                    "${featurePascal}UiState.kt",
                    FeatureTemplates.uiState(featurePascal)
                )
                viewModelDir.createFileIfNotExists(
                    "${featurePascal}ViewModel.kt",
                    FeatureTemplates.viewModel(featurePascal)
                )
                uiDir.createFileIfNotExists(
                    "${featurePascal}Fragment.kt",
                    FeatureTemplates.fragment(featurePascal, featureSnake)
                )
            }

            CleanArchitectureConfig.Language.JAVA -> {
                // ===== Java Domain =====
                domainModelDir.createFileIfNotExists(
                    "$featurePascal.java",
                    JavaFeatureTemplates.domainModel(featurePascal)
                )
                domainRepoDir.createFileIfNotExists(
                    "${featurePascal}Repository.java",
                    JavaFeatureTemplates.domainRepository(featurePascal)
                )
                useCaseDir.createFileIfNotExists(
                    "Get${featurePascal}ListUseCase.java",
                    JavaFeatureTemplates.useCase(featurePascal)
                )

                // ===== Java Data =====
                remoteDtoDir.createFileIfNotExists(
                    "${featurePascal}Dto.java",
                    JavaFeatureTemplates.dto(featurePascal)
                )
                remoteDir.createFileIfNotExists(
                    "${featurePascal}ApiService.java",
                    JavaFeatureTemplates.apiService(featurePascal)
                )
                entityDir.createFileIfNotExists(
                    "${featurePascal}Entity.java",
                    JavaFeatureTemplates.entity(featurePascal)
                )
                daoDir.createFileIfNotExists(
                    "${featurePascal}Dao.java",
                    JavaFeatureTemplates.dao(featurePascal)
                )
                repoImplDir.createFileIfNotExists(
                    "${featurePascal}RepositoryImpl.java",
                    JavaFeatureTemplates.repositoryImpl(featurePascal)
                )

                // ===== Java Presentation =====
                stateDir.createFileIfNotExists(
                    "${featurePascal}UiState.java",
                    JavaFeatureTemplates.uiState(featurePascal)
                )
                viewModelDir.createFileIfNotExists(
                    "${featurePascal}ViewModel.java",
                    JavaFeatureTemplates.viewModel(featurePascal)
                )
                uiDir.createFileIfNotExists(
                    "${featurePascal}Fragment.java",
                    JavaFeatureTemplates.fragment(featurePascal, featureSnake)
                )
            }

            CleanArchitectureConfig.Language.FLUTTER -> {
                // لسه مفيش Generation للـ Flutter → ممكن تضيفه بعدين
                return
            }
        }

        // DI (هنا ندّيها للـ Strategy)
        diStrategy.createFeatureDiModule(diDir, featurePascal)

        // مشتركة بين اللغتين (XML + AppDatabase + NavGraph)
        generateLayoutXml(project, featureSnake)
        updateAppDatabaseForFeature(project, featurePascal, featureCamel, config.language)
        updateNavGraphForFeature(project, featurePascal, featureSnake)
    }


    private fun PsiDirectory.createFileIfNotExists(
        fileName: String,
        content: String
    ): PsiFile {
        val existing = findFile(fileName)
        if (existing != null) return existing

        val file = createFile(fileName)
        file.viewProvider.document?.setText(content.trimIndent())
        return file
    }

    private fun ensureCoreFiles(
        psiSrcRoot: PsiDirectory,
        diStrategy: DiStrategy,
        language: CleanArchitectureConfig.Language
    ) {
        val coreDir = psiSrcRoot.findSubdirectory("core") ?: psiSrcRoot.createSubdirectory("core")
        val utilsDir = coreDir.findSubdirectory("utils") ?: coreDir.createSubdirectory("utils")
        val databaseDir = coreDir.findSubdirectory("database") ?: coreDir.createSubdirectory("database")

        // دي هتتولى إنشاء ملفات ال-Core و DI حسب الاستراتيجية (Hilt/Koin/Dagger) + اللغة
        diStrategy.ensureCoreDiFiles(coreDir, utilsDir, databaseDir)
    }


    private fun generateLayoutXml(project: Project, featureSnake: String) {
        val baseDir = project.baseDir ?: return
        val resDir: VirtualFile = VfsUtil.findRelativeFile(baseDir, "app", "src", "main", "res") ?: return
        val layoutDir = VfsUtil.findRelativeFile(resDir, "layout") ?: resDir.createChildDirectory(this, "layout")

        val fileName = "fragment_${featureSnake}.xml"
        val existing = layoutDir.findChild(fileName)
        if (existing != null) return

        val file = layoutDir.createChildData(this, fileName)
        val content = """
        <?xml version="1.0" encoding="utf-8"?>
        <androidx.constraintlayout.widget.ConstraintLayout
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:id="@+id/root"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <ProgressBar
                android:id="@+id/progressBar"
                android:layout_width="48dp"
                android:layout_height="48dp"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintEnd_toEndOf="parent" />

            <LinearLayout
                android:id="@+id/errorGroup"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical"
                android:visibility="gone"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintBottom_toBottomOf="parent">

                <TextView
                    android:id="@+id/errorText"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Something went wrong" />
            </LinearLayout>

            <LinearLayout
                android:id="@+id/contentGroup"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical"
                android:visibility="gone">

                <!-- TODO: RecyclerView or content view -->
            </LinearLayout>

        </androidx.constraintlayout.widget.ConstraintLayout>
    """.trimIndent()

        VfsUtil.saveText(file, content)
    }


    private fun updateAppDatabaseForFeature(
        project: Project,
        featurePascal: String,
        featureCamel: String,
        language: CleanArchitectureConfig.Language
    ) {
        val baseDir = project.baseDir ?: return

        val dbFileName = when (language) {
            CleanArchitectureConfig.Language.KOTLIN -> "AppDatabase.kt"
            CleanArchitectureConfig.Language.JAVA -> "AppDatabase.java"
            CleanArchitectureConfig.Language.FLUTTER -> return // مفيش Room في Flutter هنا
        }

        val dbVirtualFile = VfsUtil.findRelativeFile(
            baseDir,
            "app", "src", "main", "java", "core", "database", dbFileName
        ) ?: return

        val psiFile = PsiManager.getInstance(project).findFile(dbVirtualFile) ?: return
        val document = psiFile.viewProvider.document ?: return

        val entityName = "${featurePascal}Entity"
        val daoName = "${featurePascal}Dao"

        updateAppDatabaseImports(document, featurePascal)
        updateAppDatabaseEntities(document, entityName, language)
        updateAppDatabaseDaoMethod(document, featureCamel, daoName, language)
    }

    private fun updateAppDatabaseImports(
        document: Document,
        featurePascal: String
    ) {
        val featureLower = featurePascal.lowercase()
        val importLine =
            "import features.$featureLower.data.local.entity.${featurePascal}Entity"

        val text = document.text
        if (text.contains(importLine)) return

        val packageIndex = text.indexOf("package ")
        if (packageIndex == -1) return

        val firstImportIndex = text.indexOf("import ", startIndex = packageIndex)
        val insertOffset = if (firstImportIndex != -1) {
            firstImportIndex
        } else {
            text.indexOf('\n', packageIndex).let { if (it == -1) text.length else it + 1 }
        }

        document.insertString(insertOffset, "$importLine\n")
    }

    private fun updateAppDatabaseEntities(
        document: Document,
        entityName: String,
        language: CleanArchitectureConfig.Language
    ) {
        val text = document.text
        val annotationIndex = text.indexOf("@Database(")
        if (annotationIndex == -1) return

        val entitiesIndex = text.indexOf("entities", startIndex = annotationIndex)
        if (entitiesIndex == -1) return

        val (startBracketChar, endBracketChar, suffix) = when (language) {
            CleanArchitectureConfig.Language.KOTLIN ->
                Triple('[', ']', "::class")
            CleanArchitectureConfig.Language.JAVA ->
                Triple('{', '}', ".class")
            CleanArchitectureConfig.Language.FLUTTER ->
                return
        }

        val startBracket = text.indexOf(startBracketChar, startIndex = entitiesIndex)
        val endBracket = text.indexOf(endBracketChar, startIndex = startBracket)
        if (startBracket == -1 || endBracket == -1) return

        val currentEntities = text.substring(startBracket + 1, endBracket).trim()

        val entityToken = "$entityName$suffix"

        // Already added?
        if (currentEntities.contains(entityToken)) return

        val newEntities = when {
            currentEntities.isEmpty() -> entityToken
            else -> "$currentEntities, $entityToken"
        }

        document.replaceString(startBracket + 1, endBracket, " $newEntities ")
    }

    private fun updateAppDatabaseDaoMethod(
        document: Document,
        featureCamel: String,
        daoName: String,
        language: CleanArchitectureConfig.Language
    ) {
        val text = document.text

        when (language) {
            CleanArchitectureConfig.Language.KOTLIN -> {
                val methodSignature = "abstract fun ${featureCamel}Dao()"
                if (text.contains(methodSignature)) return

                val classEndIndex = text.lastIndexOf('}')
                if (classEndIndex == -1) return

                val insertText = "\n    abstract fun ${featureCamel}Dao(): $daoName\n"
                document.insertString(classEndIndex, insertText)
            }

            CleanArchitectureConfig.Language.JAVA -> {
                // نكتفي بالبحث عن اسم الميثود
                if (text.contains("${featureCamel}Dao(")) return

                val classEndIndex = text.lastIndexOf('}')
                if (classEndIndex == -1) return

                val insertText = "\n    public abstract $daoName ${featureCamel}Dao();\n"
                document.insertString(classEndIndex, insertText)
            }

            CleanArchitectureConfig.Language.FLUTTER -> {
                // مفيش Room هنا
                return
            }
        }
    }



    private fun updateAppDatabaseImports(document: Document, psiFile: PsiFile, featurePascal: String) {
        val featureLower = featurePascal.lowercase()
        val importLine = "import features.$featureLower.data.local.entity.${featurePascal}Entity"

        val text = document.text
        if (text.contains(importLine)) return

        val packageIndex = text.indexOf("package ")
        val firstImportIndex = text.indexOf("import ", startIndex = packageIndex)

        val insertOffset = if (firstImportIndex != -1) firstImportIndex else text.indexOf('\n', packageIndex) + 1

        document.insertString(insertOffset, "$importLine\n")
    }

    private fun updateAppDatabaseEntities(document: Document, entityName: String) {
        val text = document.text
        val annotationIndex = text.indexOf("@Database(")
        if (annotationIndex == -1) return

        val entitiesIndex = text.indexOf("entities", startIndex = annotationIndex)
        if (entitiesIndex == -1) return

        val startBracket = text.indexOf('[', startIndex = entitiesIndex)
        val endBracket = text.indexOf(']', startIndex = startBracket)
        if (startBracket == -1 || endBracket == -1) return

        val currentEntities = text.substring(startBracket + 1, endBracket).trim()

        // Already added?
        if (currentEntities.contains("$entityName::class")) return

        val newEntities = when {
            currentEntities.isEmpty() -> "$entityName::class"
            else -> "$currentEntities, $entityName::class"
        }

        document.replaceString(startBracket + 1, endBracket, " $newEntities ")
    }

    private fun updateAppDatabaseDaoMethod(
        document: Document,
        featureCamel: String,
        daoName: String
    ) {
        val text = document.text
        val methodSignature = "abstract fun ${featureCamel}Dao()"

        if (text.contains(methodSignature)) return

        val classEndIndex = text.lastIndexOf('}')
        if (classEndIndex == -1) return

        val insertText = "\n    abstract fun ${featureCamel}Dao(): $daoName\n"

        document.insertString(classEndIndex, insertText)
    }

    private fun updateNavGraphForFeature(
        project: Project,
        featurePascal: String,
        featureSnake: String
    ) {
        val baseDir = project.baseDir ?: return
        val navFile = VfsUtil.findRelativeFile(
            baseDir,
            "app", "src", "main", "res", "navigation", "nav_graph.xml"
        ) ?: return

        val psiFile = PsiManager.getInstance(project).findFile(navFile) ?: return
        val document = psiFile.viewProvider.document ?: return

        val featureLower = featurePascal.lowercase()
        val fragmentClass = "features.$featureLower.presentation.ui.${featurePascal}Fragment"
        val fragmentId = "@+id/${featureSnake}_fragment"

        val text = document.text

        // لو الـ fragment متضافش قبل كده
        if (text.contains(fragmentClass) || text.contains(fragmentId)) {
            return
        }

        val insertIndex = text.lastIndexOf("</navigation>")
        if (insertIndex == -1) return

        val fragmentTag = """
        
        <fragment
            android:id="$fragmentId"
            android:name="$fragmentClass"
            android:label="$featurePascal"
            tools:layout="@layout/fragment_${featureSnake}" />
        
    """.trimIndent()

        document.insertString(insertIndex, "\n$fragmentTag\n")
    }


}