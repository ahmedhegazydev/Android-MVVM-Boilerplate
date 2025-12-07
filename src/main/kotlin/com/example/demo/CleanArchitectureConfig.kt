package com.example.demo

data class CleanArchitectureConfig(
    val className: String,
    val language: Language = Language.KOTLIN,
    val di: DependencyInjection = DependencyInjection.HILT,
) {
    enum class Language { JAVA, KOTLIN, FLUTTER }
    enum class DependencyInjection { HILT, KOIN, DAGGER }
}
