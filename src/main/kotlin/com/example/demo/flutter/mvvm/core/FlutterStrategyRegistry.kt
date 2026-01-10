package com.example.demo.flutter.mvvm.core

import com.example.demo.CleanArchitectureConfig

object FlutterStrategyRegistry {

    private val strategies = mutableListOf<FlutterMvvmStrategy>()

    fun register(strategy: FlutterMvvmStrategy) {
        strategies += strategy
    }

    fun resolve(config: CleanArchitectureConfig): FlutterMvvmStrategy {
        return strategies.firstOrNull {
            it.stateManagement == config.state &&
                    it.supportedDi.contains(config.di)
        } ?: error(
            "No Flutter strategy found for state=${config.state}, di=${config.di}"
        )
    }
}
