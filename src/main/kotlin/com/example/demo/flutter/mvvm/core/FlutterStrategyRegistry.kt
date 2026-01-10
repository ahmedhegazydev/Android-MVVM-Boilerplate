package com.example.demo.flutter.mvvm.core

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.bloc.BlocFlutterStrategy
import com.example.demo.flutter.mvvm.cubit.CubitFlutterStrategy
import com.example.demo.flutter.mvvm.provider.ProviderFlutterStrategy
import com.example.demo.flutter.mvvm.riverpod.RiverpodFlutterStrategy

object FlutterStrategyRegistry {

    private var strategies: List<FlutterMvvmStrategy> = listOf(
        BlocFlutterStrategy,
        CubitFlutterStrategy,
        ProviderFlutterStrategy,
        RiverpodFlutterStrategy,
    )

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
