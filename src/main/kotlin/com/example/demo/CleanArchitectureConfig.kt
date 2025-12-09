package com.example.demo


data class CleanArchitectureConfig(
    val className: String,
    val language: Language,
    val di: DependencyInjection,
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
        RIVERPOD,
        PROVIDER,

        // Fallback
        NONE,
    }
}
