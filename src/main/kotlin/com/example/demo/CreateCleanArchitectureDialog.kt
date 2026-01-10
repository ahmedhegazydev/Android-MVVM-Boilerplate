package com.example.demo

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBUI
import java.awt.*
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.TitledBorder

class CreateCleanArchitectureDialog(project: Project) : DialogWrapper(project, true) {

    private val classNameField = JTextField()

    private val javaRadio = JRadioButton("Java")
    private val kotlinRadio = JRadioButton("Kotlin", true)
    private val flutterRadio = JRadioButton("Flutter")

    private val diOption1Radio = JRadioButton()
    private val diOption2Radio = JRadioButton()
    private val diOption3Radio = JRadioButton()
    private val diLabel = JLabel("Dependency Injection")

    private val getItCheckBox = JCheckBox("GetIt")

    private val stateLabel = JLabel("State Management")
    private val riverpodRadio = JRadioButton("Riverpod", true)
    private val providerRadio = JRadioButton("Provider")
    private val blocRadio = JRadioButton("BLoC")
    private val cubitRadio = JRadioButton("Cubit")
    private val stateGroup = ButtonGroup()

    private var currentLanguage: CleanArchitectureConfig.Language =
        CleanArchitectureConfig.Language.KOTLIN

    var result: CleanArchitectureConfig? = null
        private set

    // Panels (to hide/show nicely)
    private lateinit var diSection: JPanel
    private lateinit var flutterSection: JPanel

    init {
        title = "Create Clean Architecture"
        isResizable = false

        init()

    }

    override fun createCenterPanel(): JComponent {
        val root = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createEmptyBorder(0, 14, 14, 14)
            preferredSize = Dimension(440, 340)
        }

        val gbc = GridBagConstraints().apply {
            gridx = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.NORTHWEST
            insets = JBUI.insetsBottom(12)
        }

        // ================= Header =================
        gbc.gridy = 0
        root.add(createHeaderPanelFixed(dialogWidth = 440, heightPx = 300), gbc)

        // ================= Basics =================
        gbc.gridy = 1
        val basics = section("Basics").apply {
            layout = GridBagLayout()
        }

        val b = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
            insets = JBUI.insetsBottom(6)
        }

        basics.add(JLabel("Class Name"), b)

        b.gridy++
        classNameField.maximumSize =
            Dimension(Int.MAX_VALUE, classNameField.preferredSize.height)
        basics.add(classNameField, b)

        b.gridy++
        b.insets = JBUI.insets(10, 0, 4, 0)
        basics.add(JLabel("Language"), b)

        b.gridy++
        b.insets = JBUI.emptyInsets()

        val langRow = JPanel(FlowLayout(FlowLayout.LEFT, 12, 0)).apply {
            add(kotlinRadio)
            add(javaRadio)
            add(flutterRadio)
        }

        ButtonGroup().apply {
            add(kotlinRadio)
            add(javaRadio)
            add(flutterRadio)
        }

        basics.add(langRow, b)
        root.add(basics, gbc)

        // ================= DI Section =================
        gbc.gridy = 2
        diSection = section("Dependency Injection").apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        diLabel.alignmentX = JComponent.LEFT_ALIGNMENT
        diSection.add(diLabel)
        diSection.add(Box.createVerticalStrut(6))

        ButtonGroup().apply {
            add(diOption1Radio)
            add(diOption2Radio)
            add(diOption3Radio)
        }

        listOf(diOption1Radio, diOption2Radio, diOption3Radio).forEach {
            it.alignmentX = JComponent.LEFT_ALIGNMENT
            diSection.add(it)
            diSection.add(Box.createVerticalStrut(4))
        }

        root.add(diSection, gbc)

        // ================= Flutter Section =================
        gbc.gridy = 3
        flutterSection = section("Flutter Setup").apply {
            layout = GridBagLayout()
        }

        val f = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
            insets = JBUI.insetsBottom(8)
        }

        flutterSection.add(getItCheckBox, f)

        f.gridy++
        flutterSection.add(stateLabel, f)

        stateGroup.add(riverpodRadio)
        stateGroup.add(providerRadio)
        stateGroup.add(blocRadio)
        stateGroup.add(cubitRadio)

        val stateGrid = JPanel(GridLayout(2, 2, 12, 6)).apply {
            add(riverpodRadio)
            add(providerRadio)
            add(blocRadio)
            add(cubitRadio)
        }

        f.gridy++
        f.insets = JBUI.emptyInsets()
        flutterSection.add(stateGrid, f)

        root.add(flutterSection, gbc)

        // ================= Glue =================
        gbc.gridy = 4
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        root.add(Box.createGlue(), gbc)

        // ================= Listeners =================
        val updateLanguage = {
            currentLanguage = when {
                kotlinRadio.isSelected -> CleanArchitectureConfig.Language.KOTLIN
                javaRadio.isSelected -> CleanArchitectureConfig.Language.JAVA
                else -> CleanArchitectureConfig.Language.FLUTTER
            }
            configureForLanguage(currentLanguage)
            root.revalidate()
            root.repaint()
        }

        kotlinRadio.addActionListener { updateLanguage() }
        javaRadio.addActionListener { updateLanguage() }
        flutterRadio.addActionListener { updateLanguage() }

        configureForLanguage(currentLanguage)

        return root
    }

    override fun isResizable(): Boolean = false

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

            CleanArchitectureConfig.Language.JAVA -> CleanArchitectureConfig.DependencyInjection.DAGGER

            CleanArchitectureConfig.Language.FLUTTER -> {
                if (getItCheckBox.isSelected) CleanArchitectureConfig.DependencyInjection.GET_IT
                else CleanArchitectureConfig.DependencyInjection.NONE
            }
        }

        val state = if (lang == CleanArchitectureConfig.Language.FLUTTER) {
            when {
                riverpodRadio.isSelected -> CleanArchitectureConfig.StateManagement.RIVERPOD
                providerRadio.isSelected -> CleanArchitectureConfig.StateManagement.PROVIDER
                blocRadio.isSelected -> CleanArchitectureConfig.StateManagement.BLOC
                else -> CleanArchitectureConfig.StateManagement.CUBIT
            }
        } else null

        result = CleanArchitectureConfig(
            className = name,
            language = lang,
            di = di,
            state = state
        )

        super.doOKAction()
    }

    private fun createHeaderPanelFixed(dialogWidth: Int = 440, heightPx: Int = 300): JComponent {
        val url = javaClass.classLoader.getResource("images/mvvm_header.png")
            ?: error("mvvm_header.png not found in resources/images")

        // حساب عرض المحتوى الداخلي (dialog width - left/right root padding)
        // root padding عندك: EmptyBorder(12, 14, 14, 14) => left+right = 28
        val contentWidth = dialogWidth - (14 * 2)

        val original = ImageIcon(url).image
        val scaled = original.getScaledInstance(contentWidth, heightPx, Image.SCALE_SMOOTH)

        return JPanel(BorderLayout()).apply {
            isOpaque = false
            border = null
            preferredSize = Dimension(contentWidth, heightPx)
            minimumSize = Dimension(contentWidth, heightPx)
            maximumSize = Dimension(contentWidth, heightPx)

            add(JLabel(ImageIcon(scaled)).apply {
                horizontalAlignment = SwingConstants.CENTER
                verticalAlignment = SwingConstants.TOP
            }, BorderLayout.CENTER)
        }
    }

    private fun fixedSizePanel(content: JComponent, w: Int? = null, h: Int? = null): JComponent {
        val panel = JPanel(BorderLayout())
        panel.isOpaque = false
        panel.add(content, BorderLayout.CENTER)

        val pw = w ?: content.preferredSize.width
        val ph = h ?: content.preferredSize.height

        panel.preferredSize = Dimension(pw, ph)
        panel.minimumSize = Dimension(pw, ph)
        panel.maximumSize = Dimension(pw, ph)

        return panel
    }



    private fun configureForLanguage(language: CleanArchitectureConfig.Language) {
        when (language) {
            CleanArchitectureConfig.Language.KOTLIN -> {
                // show DI section
                diSection.isVisible = true
                flutterSection.isVisible = false

                diLabel.text = "Choose your DI"
                diOption1Radio.text = "Hilt"
                diOption2Radio.text = "Koin"
                diOption3Radio.text = ""

                diOption1Radio.isVisible = true
                diOption2Radio.isVisible = true
                diOption3Radio.isVisible = false

                diOption1Radio.isEnabled = true
                diOption2Radio.isEnabled = true
                diOption3Radio.isEnabled = false

                diOption1Radio.isSelected = true
            }

            CleanArchitectureConfig.Language.JAVA -> {
                diSection.isVisible = true
                flutterSection.isVisible = false

                diLabel.text = "Choose your DI"
                diOption1Radio.text = "Dagger"
                diOption2Radio.text = ""
                diOption3Radio.text = ""

                diOption1Radio.isVisible = true
                diOption2Radio.isVisible = false
                diOption3Radio.isVisible = false

                diOption1Radio.isEnabled = true
                diOption2Radio.isEnabled = false
                diOption3Radio.isEnabled = false

                diOption1Radio.isSelected = true
            }

            CleanArchitectureConfig.Language.FLUTTER -> {
                diSection.isVisible = false
                flutterSection.isVisible = true

                getItCheckBox.isSelected = true
                riverpodRadio.isSelected = true
            }
        }
    }

    private fun section(title: String): JPanel {
        val panel = JPanel()
        val border = TitledBorder(title)
        panel.border = CompoundBorder(border, EmptyBorder(10, 12, 12, 12))
        panel.alignmentX = JComponent.LEFT_ALIGNMENT
        return panel
    }
}
