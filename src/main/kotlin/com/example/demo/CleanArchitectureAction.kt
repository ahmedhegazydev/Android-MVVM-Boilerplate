package com.example.demo

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class CleanArchitectureAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
//        Messages.showInfoMessage("🚀 Clean Architecture Generator starting!", "Success")

        val project: Project = e.project ?: return

        val dialog = CreateCleanArchitectureDialog(project)
        if (!dialog.showAndGet()) {
            // user pressed Cancel
            return
        }

        val config = dialog.result ?: return

        // TODO: replace with your real file/folder generation
        WriteCommandAction.runWriteCommandAction(project) {
            Messages.showInfoMessage(
                project,
                "Will generate Clean Architecture for: ${config.className}\n" +
                        "Language: ${config.language}\n" +
                        "DI: ${config.di}",
                "Clean Architecture"
            )
        }
    }
}
