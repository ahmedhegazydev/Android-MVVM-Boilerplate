package com.example.demo.flutter.mvvm.shared


object FeaturePathProfiles {

    /** bloc/cubit/riverpod use mvvm folders: domain/model, domain/repository, data/remote, ... */
    val MVVM = FeatureTemplates.Paths(
        domainModelImport = { snake -> "../model/${snake}_model.dart" },
        domainRepoImport = { snake -> "../repository/${snake}_repository.dart" },

        dataModelImport = { snake -> "../../domain/model/${snake}_model.dart" },
        dataRepoImport = { snake -> "../../domain/repository/${snake}_repository.dart" },
        dataRemoteImport = { snake -> "../remote/${snake}_api_service.dart" },

        useCaseImportRepo = { snake -> "../repository/${snake}_repository.dart" },
        useCaseImportModel = { snake -> "../model/${snake}_model.dart" },
    )

    /** provider templates currently live in same folder (flat imports). */
    val FLAT = FeatureTemplates.Paths(
        domainModelImport = { snake -> "${snake}_model.dart" },
        domainRepoImport = { snake -> "${snake}_repository.dart" },

        dataModelImport = { snake -> "${snake}_model.dart" },
        dataRepoImport = { snake -> "${snake}_repository.dart" },
        dataRemoteImport = { snake -> "${snake}_api_service.dart" },

        useCaseImportRepo = { snake -> "${snake}_repository.dart" },
        useCaseImportModel = { snake -> "${snake}_model.dart" },
    )
}
