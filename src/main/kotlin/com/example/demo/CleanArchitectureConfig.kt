package com.example.demo


data class CleanArchitectureConfig(
    val className: String,
    val language: Language,
    val di: DependencyInjection,
    val state: StateManagement? = null, // Flutter only (nullable for Kotlin/Java)
) {

    enum class Language {
        JAVA,
        KOTLIN,
        FLUTTER,
    }

    enum class DependencyInjection {
        // Android DI
        HILT,      // Kotlin
        KOIN,      // Kotlin or Java (لو حابب)
        DAGGER,    // Java only

        // Flutter DI
        GET_IT,

        // Fallback
        NONE,
    }

    enum class StateManagement { RIVERPOD, PROVIDER, BLOC, CUBIT }

}
