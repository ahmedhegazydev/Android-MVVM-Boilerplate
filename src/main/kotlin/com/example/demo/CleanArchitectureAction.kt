package com.example.demo

import com.example.demo.FileGenerator
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager

class CleanArchitectureAction : AnAction() {

    companion object {
        private val LOG = Logger.getInstance(CleanArchitectureAction::class.java)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        if (isRunning) return

        // 1) Show dialog
        val dialog = CreateCleanArchitectureDialog(project)
        if (!dialog.showAndGet()) return

        val config = dialog.result ?: return

        isRunning = true

        try {
            // 2) Run with modal progress (safe for PSI/VFS + write command)
            ProgressManager.getInstance().runProcessWithProgressSynchronously(
                {
                    val indicator = ProgressManager.getInstance().progressIndicator
                    indicator?.isIndeterminate = false
                    indicator?.fraction = 0.05
                    indicator?.text = "Preparing generation…"

                    // 3) All model modifications inside WriteCommandAction
                    WriteCommandAction.runWriteCommandAction(project) {
                        indicator?.text = "Generating files…"
                        indicator?.fraction = 0.4

                        FileGenerator.generate(project, config)

                        indicator?.text = "Finishing…"
                        indicator?.fraction = 0.8
                    }

                    // 4) Refresh VFS after write
                    indicator?.text = "Refreshing project…"
                    indicator?.fraction = 1.0
                },
                "Generating Clean Architecture for '${config.className}'",
                false, // isCancelable
                project
            )


            // 2️⃣ الآن رجعنا على الـ EDT → refresh آمن هنا
            VirtualFileManager.getInstance().syncRefresh()

            // 3️⃣ success dialog
//            Messages.showInfoMessage(
//                project,
//                "🚀 Generated Clean Architecture for '${config.className}'\n\n" +
//                        "Language: ${config.language}\n" +
//                        "DI: ${config.di}",
//                "Generation Complete"
//            )
            NotificationGroupManager.getInstance()
                .getNotificationGroup("CleanArchitectureGenerator")
                .createNotification(
                    "Generation Complete",
                    """
                        🚀 Generated Clean Architecture for <b>${config.className}</b><br/>
                        Language: ${config.language}<br/>
                        DI: ${config.di}
                    """.trimIndent(),
                    NotificationType.INFORMATION
                )
                .notify(project)

        } catch (t: Throwable) {
            LOG.error("Failed to generate Clean Architecture for ${config.className}", t)
            // You can keep dialog for errors, or also switch to a balloon
            Messages.showErrorDialog(
                project,
                "Failed to generate files:\n${t.message ?: "Unknown error"}",
                "Generation Error"
            )
        } finally {
            isRunning = false
        }


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
        // update() only changes presentation, so EDT is fine
        return ActionUpdateThread.EDT
    }
}

@Volatile
private var isRunning: Boolean = false
