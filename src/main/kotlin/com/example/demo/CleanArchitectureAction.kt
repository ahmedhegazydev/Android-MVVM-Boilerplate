package com.example.demo

import com.example.demo.helpers.FileGenerator
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.application.ApplicationManager

class CleanArchitectureAction : AnAction() {

    companion object {
        private val LOG = Logger.getInstance(CleanArchitectureAction::class.java)

        @Volatile
        private var isRunning: Boolean = false
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        if (isRunning) return

        val dialog = CreateCleanArchitectureDialog(project)
        if (!dialog.showAndGet()) return

        val config = dialog.result ?: return

        isRunning = true

        ProgressManager.getInstance().run(object : Task.Backgroundable(
            project,
            "Generating Clean Architecture for '${config.className}'",
            false
        ) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = false
                indicator.fraction = 0.05
                indicator.text = "Preparing generation..."

                try {
                    indicator.text = "Writing files..."
                    indicator.fraction = 0.3

                    // كل الكتابة في WriteCommandAction
                    WriteCommandAction.runWriteCommandAction(project) {
                        FileGenerator.generate(project, config)
                    }

                    indicator.text = "Refreshing project..."
                    indicator.fraction = 0.8

                    VirtualFileManager.getInstance().syncRefresh()

                    indicator.fraction = 1.0
                    indicator.text = "Done"

                    ApplicationManager.getApplication().invokeLater {
                        Messages.showInfoMessage(
                            project,
                            "🚀 Generated Clean Architecture for '${config.className}'\n\n" +
                                    "Language: ${config.language}\n" +
                                    "DI: ${config.di}",
                            "Generation Complete"
                        )
                    }
                } catch (t: Throwable) {
                    LOG.error("Failed to generate Clean Architecture for ${config.className}", t)

                    ApplicationManager.getApplication().invokeLater {
                        Messages.showErrorDialog(
                            project,
                            "Failed to generate files:\n${t.message ?: "Unknown error"}",
                            "Generation Error"
                        )
                    }
                } finally {
                    isRunning = false
                }
            }
        })
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isVisible = project != null

        if (project == null) {
            e.presentation.isEnabled = false
            return
        }

        if (isRunning) {
            e.presentation.isEnabled = false
            e.presentation.text = "Generating Clean Architecture…"
        } else {
            e.presentation.isEnabled = true
            e.presentation.text = "Create Clean Architecture"
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        // update() بس بيعدل presentation → ينفع Background
        return ActionUpdateThread.BGT
    }
}
