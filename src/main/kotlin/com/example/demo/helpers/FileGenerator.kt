package com.example.demo.helpers


import com.example.demo.CleanArchitectureConfig
import com.example.demo.FeatureTemplates
import com.example.demo.helpers.NameUtils.toCamelCase
import com.example.demo.helpers.NameUtils.toPascalCase
import com.example.demo.helpers.NameUtils.toSnakeCase
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

object FileGenerator {

    fun generate(project: Project, config: CleanArchitectureConfig) {
        if (config.language != CleanArchitectureConfig.Language.KOTLIN ||
            config.di != CleanArchitectureConfig.DependencyInjection.HILT
        ) {
            // حاليا ندعم Kotlin + Hilt بس
            return
        }

        val baseDir = project.baseDir ?: return
        val psiManager = PsiManager.getInstance(project)

        // app/src/main/java
        val srcRoot = VfsUtil.findRelativeFile(baseDir, "app", "src", "main", "java") ?: return
        val psiSrcRoot = psiManager.findDirectory(srcRoot) ?: return

        // تأكد إن core files موجودة
        ensureCoreFiles(psiSrcRoot)

        // أنشئ feature
        generateFeature(project, psiSrcRoot, config)


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

    private fun generateFeature(
        project: Project,
        psiSrcRoot: PsiDirectory,
        config: CleanArchitectureConfig
    ) {
        val featurePascal = config.className.toPascalCase()   // Example
        val featureCamel = config.className.toCamelCase()
        val featureSnake = config.className.toSnakeCase()

        // features/<featurename>/
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

        // Domain
        domainModelDir.createFileIfNotExists("${featurePascal}.kt", FeatureTemplates.domainModel(featurePascal))
        domainRepoDir.createFileIfNotExists(
            "${featurePascal}Repository.kt",
            FeatureTemplates.domainRepository(featurePascal)
        )
        useCaseDir.createFileIfNotExists("Get${featurePascal}ListUseCase.kt", FeatureTemplates.useCase(featurePascal))

        // Data
        remoteDtoDir.createFileIfNotExists("${featurePascal}Dto.kt", FeatureTemplates.dto(featurePascal))
        remoteDir.createFileIfNotExists("${featurePascal}ApiService.kt", FeatureTemplates.apiService(featurePascal))
        entityDir.createFileIfNotExists("${featurePascal}Entity.kt", FeatureTemplates.entity(featurePascal))
        daoDir.createFileIfNotExists("${featurePascal}Dao.kt", FeatureTemplates.dao(featurePascal))
        repoImplDir.createFileIfNotExists(
            "${featurePascal}RepositoryImpl.kt",
            FeatureTemplates.repositoryImpl(featurePascal)
        )

        // DI
        diDir.createFileIfNotExists("${featurePascal}Module.kt", FeatureTemplates.diModule(featurePascal))

        // Presentation
        stateDir.createFileIfNotExists("${featurePascal}UiState.kt", FeatureTemplates.uiState(featurePascal))
        viewModelDir.createFileIfNotExists("${featurePascal}ViewModel.kt", FeatureTemplates.viewModel(featurePascal))
        uiDir.createFileIfNotExists(
            "${featurePascal}Fragment.kt",
            FeatureTemplates.fragment(featurePascal, featureSnake)
        )

        // XML layout (under res/layout)
        generateLayoutXml(project, featureSnake)

        // 🔥 Update shared parts
        updateAppDatabaseForFeature(project, featurePascal, featureCamel)
        updateNavGraphForFeature(project, featurePascal, featureSnake)

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

    private fun ensureCoreFiles(psiSrcRoot: PsiDirectory) {
        // package core.common, core.utils, core.di, core.database
        val coreDir = psiSrcRoot.findSubdirectory("core") ?: psiSrcRoot.createSubdirectory("core")
        val commonDir = coreDir.findSubdirectory("common") ?: coreDir.createSubdirectory("common")
        val utilsDir = coreDir.findSubdirectory("utils") ?: coreDir.createSubdirectory("utils")
        val diDir = coreDir.findSubdirectory("di") ?: coreDir.createSubdirectory("di")
        val databaseDir = coreDir.findSubdirectory("database") ?: coreDir.createSubdirectory("database")

        // Resource.kt
        commonDir.createFileIfNotExists("Resource.kt", CoreTemplates.resource)

        // DispatcherProvider.kt
        commonDir.createFileIfNotExists("DispatcherProvider.kt", CoreTemplates.dispatcherProvider)

        // BaseViewModel.kt
        commonDir.createFileIfNotExists("BaseViewModel.kt", CoreTemplates.baseViewModel)

        // BaseFragment.kt
        commonDir.createFileIfNotExists("BaseFragment.kt", CoreTemplates.baseFragment)

        // ErrorHandler.kt
        commonDir.createFileIfNotExists("ErrorHandler.kt", CoreTemplates.errorHandler)

        // Constants.kt
        utilsDir.createFileIfNotExists("Constants.kt", CoreTemplates.constants)

        // NetworkModule.kt
        diDir.createFileIfNotExists("NetworkModule.kt", CoreTemplates.networkModule)

        // DatabaseModule.kt
        diDir.createFileIfNotExists("DatabaseModule.kt", CoreTemplates.databaseModule)

        // DispatcherModule.kt
        diDir.createFileIfNotExists("DispatcherModule.kt", CoreTemplates.dispatcherModule)

        // AppDatabase.kt
        databaseDir.createFileIfNotExists("AppDatabase.kt", CoreTemplates.appDatabase)
    }

    private fun updateAppDatabaseForFeature(
        project: Project,
        featurePascal: String,
        featureCamel: String
    ) {
        val baseDir = project.baseDir ?: return
        val dbVirtualFile = VfsUtil.findRelativeFile(
            baseDir,
            "app", "src", "main", "java", "core", "database", "AppDatabase.kt"
        ) ?: return

        val psiFile = PsiManager.getInstance(project).findFile(dbVirtualFile) ?: return
        val document = psiFile.viewProvider.document ?: return

        val featureLower = featurePascal.lowercase()
        val entityName = "${featurePascal}Entity"
        val daoName = "${featurePascal}Dao"

        updateAppDatabaseImports(document, psiFile, featurePascal)
        updateAppDatabaseEntities(document, entityName)
        updateAppDatabaseDaoMethod(document, featureCamel, daoName)
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


