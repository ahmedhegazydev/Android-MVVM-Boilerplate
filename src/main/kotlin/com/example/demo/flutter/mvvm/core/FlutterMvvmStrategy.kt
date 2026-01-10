package com.example.demo.flutter.mvvm.core

import com.example.demo.CleanArchitectureConfig
import com.intellij.openapi.project.Project

interface FlutterMvvmStrategy {
    val id: CleanArchitectureConfig.DependencyInjection
    val stateManagement: CleanArchitectureConfig.StateManagement

    fun generateFeature(project: Project, config: CleanArchitectureConfig)
}