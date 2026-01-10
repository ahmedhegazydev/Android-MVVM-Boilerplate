package com.example.demo.flutter.mvvm.core

import com.example.demo.CleanArchitectureConfig
import com.intellij.openapi.project.Project

interface FlutterMvvmStrategy {

    val stateManagement: CleanArchitectureConfig.StateManagement
    val supportedDi: Set<CleanArchitectureConfig.DependencyInjection>


    fun generateFeature(project: Project, config: CleanArchitectureConfig)
}