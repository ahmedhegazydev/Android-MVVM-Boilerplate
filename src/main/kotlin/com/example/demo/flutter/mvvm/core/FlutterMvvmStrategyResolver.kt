package com.example.demo.flutter.mvvm.core

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.provider.ProviderFlutterStrategy

object FlutterMvvmStrategyResolver {


    fun resolve(
        strategies: List<FlutterMvvmStrategy>,
        config: CleanArchitectureConfig
    ): FlutterMvvmStrategy {
        val state = config.state
            ?: error("Flutter state is required when language=FLUTTER")

        return strategies.firstOrNull { s ->
            s.stateManagement == state && config.di in s.supportedDi
        } ?: error("No Flutter strategy found for state=$state di=${config.di}")
    }
}