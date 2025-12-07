package com.example.demo

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*


class CreateCleanArchitectureDialog(project: Project) : DialogWrapper(project, true) {

    // UI components
    private val classNameField = JTextField()

    private val javaRadio = JRadioButton("Java")
    private val kotlinRadio = JRadioButton("Kotlin", true)
    private val flutterRadio = JRadioButton("Flutter")

    private val hiltRadio = JRadioButton("Hilt", true)
    private val koinRadio = JRadioButton("Koin")
    private val daggerRadio = JRadioButton("Dagger")

    var result: CleanArchitectureConfig? = null
        private set

    init {
        title = "Create Clean Architecture Name"
        init()
    }

    override fun createCenterPanel(): JComponent {
        // Root panel (vertical)
        val root = JPanel()
        root.layout = BoxLayout(root, BoxLayout.Y_AXIS)
        root.border = BorderFactory.createEmptyBorder(12, 16, 16, 16)
        root.preferredSize = Dimension(340, 260)

        // --- Class name field ---
        val nameLabel = JLabel("Class Name")
        nameLabel.alignmentX = JComponent.LEFT_ALIGNMENT
        root.add(nameLabel)

        classNameField.alignmentX = JComponent.LEFT_ALIGNMENT
        classNameField.maximumSize = Dimension(Int.MAX_VALUE, classNameField.preferredSize.height)
        root.add(Box.createVerticalStrut(4))
        root.add(classNameField)
        root.add(Box.createVerticalStrut(16))

        // --- Language group ---
        val langLabel = JLabel("Language")
        langLabel.alignmentX = JComponent.LEFT_ALIGNMENT
        root.add(langLabel)
        root.add(Box.createVerticalStrut(4))

        val langPanel = JPanel()
        langPanel.layout = BoxLayout(langPanel, BoxLayout.Y_AXIS)
        langPanel.alignmentX = JComponent.LEFT_ALIGNMENT

        val langGroup = ButtonGroup()
        langGroup.add(javaRadio)
        langGroup.add(kotlinRadio)
        langGroup.add(flutterRadio)

        // disable Java & Flutter like in your screenshot
//        javaRadio.isEnabled = false
//        flutterRadio.isEnabled = false

        langPanel.add(kotlinRadio)
        langPanel.add(javaRadio)
        langPanel.add(flutterRadio)
        root.add(langPanel)
        root.add(Box.createVerticalStrut(16))

        // --- DI group ---
        val diLabel = JLabel("Dependency Injection")
        diLabel.alignmentX = JComponent.LEFT_ALIGNMENT
        root.add(diLabel)
        root.add(Box.createVerticalStrut(4))

        val diPanel = JPanel()
        diPanel.layout = BoxLayout(diPanel, BoxLayout.Y_AXIS)
        diPanel.alignmentX = JComponent.LEFT_ALIGNMENT

        val diGroup = ButtonGroup()
        diGroup.add(hiltRadio)
        diGroup.add(koinRadio)
        diGroup.add(daggerRadio)

        // disable others, keep Hilt enabled & selected
//        koinRadio.isEnabled = false
//        daggerRadio.isEnabled = false

        diPanel.add(hiltRadio)
        diPanel.add(koinRadio)
        diPanel.add(daggerRadio)
        root.add(diPanel)

        return root
    }

    override fun doOKAction() {
        val name = classNameField.text.trim()
        if (name.isEmpty()) {
            setErrorText("Class name is required.")
            return
        }

        val lang = when {
            kotlinRadio.isSelected -> CleanArchitectureConfig.Language.KOTLIN
            javaRadio.isSelected -> CleanArchitectureConfig.Language.JAVA
            else -> CleanArchitectureConfig.Language.FLUTTER
        }

        val di = when {
            hiltRadio.isSelected -> CleanArchitectureConfig.DependencyInjection.HILT
            koinRadio.isSelected -> CleanArchitectureConfig.DependencyInjection.KOIN
            else -> CleanArchitectureConfig.DependencyInjection.DAGGER
        }

        result = CleanArchitectureConfig(
            className = name,
            language = lang,
            di = di,
        )

        super.doOKAction()
    }
}
