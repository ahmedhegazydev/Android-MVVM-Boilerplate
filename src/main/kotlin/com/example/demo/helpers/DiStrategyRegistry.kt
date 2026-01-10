package com.example.demo.helpers

import com.example.demo.CleanArchitectureConfig
import com.example.demo.java.mvvm.dagger.DaggerDiStrategy
import com.example.demo.kotlin.mvvm.core.DiStrategy
import com.example.demo.kotlin.mvvm.hilt.HiltDiStrategy
import com.example.demo.kotlin.mvvm.koin.KoinDiStrategy

object DiStrategyRegistry {

    private var strategies: List<DiStrategy> = listOf(
        HiltDiStrategy,   // Kotlin + Hilt
        KoinDiStrategy,   // Kotlin + Koin
        DaggerDiStrategy  // Java + Dagger
    )

    fun register(strategy: DiStrategy) {
        strategies += strategy
    }

    fun resolve(config: CleanArchitectureConfig): DiStrategy {
        val lang = config.language
        val di = config.di

        return strategies.firstOrNull { it.language == lang && it.id == di }
            ?: error("No DI strategy found for language=$lang, di=$di")
    }
}
