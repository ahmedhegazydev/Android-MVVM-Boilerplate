plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "dev.ahmedhegazy.intellij"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        androidStudio("2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
         bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            <b>Initial public release</b>
            <ul>
                <li>Android MVVM feature generation (Kotlin & Java)</li>
                <li>Flutter MVVM generators:
                    <ul>
                        <li>Provider</li>
                        <li>Riverpod</li>
                        <li>Cubit</li>
                        <li>BLoC</li>
                    </ul>
                </li>
                <li>Dependency Injection support:
                    <ul>
                        <li>Hilt</li>
                        <li>Koin</li>
                        <li>Dagger</li>
                        <li>GetIt</li>
                    </ul>
                </li>
                <li>Clean Architecture folder structure (Data / Domain / Presentation)</li>
                <li>Production-ready boilerplate with minimal setup</li>
            </ul>
        """.trimIndent()
    }

    publishing {
        token.set(System.getenv("JETBRAINS_TOKEN"))
    }
}



tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
