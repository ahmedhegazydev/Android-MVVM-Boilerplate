package com.example.demo

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import javax.swing.*

class CreateCleanArchitectureDialog(project: Project) : DialogWrapper(project, true) {

    // UI components
    private val classNameField = JTextField()

    private val javaRadio = JRadioButton("Java")
    private val kotlinRadio = JRadioButton("Kotlin", true)
    private val flutterRadio = JRadioButton("Flutter")

    // 3 DI radio buttons, we change their labels/meaning based on language
    private val diOption1Radio = JRadioButton()
    private val diOption2Radio = JRadioButton()
    private val diOption3Radio = JRadioButton()

    // Flutter: GetIt checkbox
    private val getItCheckBox = JCheckBox("GetIt")

    // Flutter: State management radios
    private val stateLabel = JLabel("State Management (Flutter)")
    private val riverpodRadio = JRadioButton("Riverpod", true)
    private val providerRadio = JRadioButton("Provider")
    private val blocRadio = JRadioButton("BLoC")
    private val cubitRadio = JRadioButton("Cubit")

    private val diLabel = JLabel("Dependency Injection")

    private var currentLanguage: CleanArchitectureConfig.Language =
        CleanArchitectureConfig.Language.KOTLIN

    var result: CleanArchitectureConfig? = null
        private set

    init {
        title = "Create Clean Architecture Name"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val root = JPanel()
        root.layout = BoxLayout(root, BoxLayout.Y_AXIS)
        root.border = BorderFactory.createEmptyBorder(12, 16, 16, 16)
        root.preferredSize = Dimension(360, 280)

        // --- Class name field ---
        val nameLabel = JLabel("Class Name")
        nameLabel.alignmentX = JComponent.LEFT_ALIGNMENT
        root.add(nameLabel)

        classNameField.alignmentX = JComponent.LEFT_ALIGNMENT
        classNameField.maximumSize =
            Dimension(Int.MAX_VALUE, classNameField.preferredSize.height)
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

        langPanel.add(kotlinRadio)
        langPanel.add(javaRadio)
        langPanel.add(flutterRadio)

        root.add(langPanel)
        root.add(Box.createVerticalStrut(16))

        // listeners to switch DI options based on language
        kotlinRadio.addActionListener {
            currentLanguage = CleanArchitectureConfig.Language.KOTLIN
            configureDiForLanguage(currentLanguage)
        }
        javaRadio.addActionListener {
            currentLanguage = CleanArchitectureConfig.Language.JAVA
            configureDiForLanguage(currentLanguage)
        }
        flutterRadio.addActionListener {
            currentLanguage = CleanArchitectureConfig.Language.FLUTTER
            configureDiForLanguage(currentLanguage)
        }

        // --- DI group ---
        diLabel.alignmentX = JComponent.LEFT_ALIGNMENT
        root.add(diLabel)
        root.add(Box.createVerticalStrut(4))

        val diPanel = JPanel()
        diPanel.layout = BoxLayout(diPanel, BoxLayout.Y_AXIS)
        diPanel.alignmentX = JComponent.LEFT_ALIGNMENT

        val diGroup = ButtonGroup()
        diGroup.add(diOption1Radio)
        diGroup.add(diOption2Radio)
        diGroup.add(diOption3Radio)

        diPanel.add(diOption1Radio)
        diPanel.add(diOption2Radio)
        diPanel.add(diOption3Radio)

        root.add(diPanel)

        // initial: Kotlin
        configureDiForLanguage(currentLanguage)

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

        val di = when (lang) {
            CleanArchitectureConfig.Language.KOTLIN -> {
                when {
                    diOption1Radio.isSelected -> CleanArchitectureConfig.DependencyInjection.HILT
                    diOption2Radio.isSelected -> CleanArchitectureConfig.DependencyInjection.KOIN
                    else -> CleanArchitectureConfig.DependencyInjection.NONE
                }
            }

            CleanArchitectureConfig.Language.JAVA -> {
                // Dagger only for Java
                CleanArchitectureConfig.DependencyInjection.DAGGER
            }

            CleanArchitectureConfig.Language.FLUTTER -> {
                when {
                    diOption1Radio.isSelected -> CleanArchitectureConfig.DependencyInjection.GET_IT
                    else -> CleanArchitectureConfig.DependencyInjection.NONE
                }
            }
        }

        result = CleanArchitectureConfig(
            className = name,
            language = lang,
            di = di,
        )

        super.doOKAction()
    }

    private fun configureDiForLanguage(language: CleanArchitectureConfig.Language) {
        when (language) {
            CleanArchitectureConfig.Language.KOTLIN -> {
                diLabel.text = "Dependency Injection (Kotlin)"

                diOption1Radio.text = "Hilt"
                diOption2Radio.text = "Koin"
                diOption3Radio.text = ""

                diOption1Radio.isEnabled = true
                diOption2Radio.isEnabled = true
                diOption3Radio.isEnabled = false

                diOption1Radio.isVisible = true
                diOption2Radio.isVisible = true
                diOption3Radio.isVisible = false

                diOption1Radio.isSelected = true
            }

            CleanArchitectureConfig.Language.JAVA -> {
                diLabel.text = "Dependency Injection (Java)"

                diOption1Radio.text = "Dagger"
                diOption2Radio.text = ""
                diOption3Radio.text = ""

                diOption1Radio.isEnabled = true
                diOption2Radio.isEnabled = false
                diOption3Radio.isEnabled = false

                diOption1Radio.isVisible = true
                diOption2Radio.isVisible = false
                diOption3Radio.isVisible = false

                diOption1Radio.isSelected = true
            }

            CleanArchitectureConfig.Language.FLUTTER -> {
                diLabel.text = "Dependency Injection (Flutter)"

                diOption1Radio.text = "GetIt"
                diOption2Radio.text = "Riverpod"
                diOption3Radio.text = "Provider"

                diOption1Radio.isEnabled = true
                diOption2Radio.isEnabled = true
                diOption3Radio.isEnabled = true

                diOption1Radio.isVisible = true
                diOption2Radio.isVisible = true
                diOption3Radio.isVisible = true

                diOption2Radio.isSelected = true // default: Riverpod
            }
        }
    }
}
