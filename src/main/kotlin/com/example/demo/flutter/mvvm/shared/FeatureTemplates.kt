package com.example.demo.flutter.mvvm.shared

import com.example.demo.CleanArchitectureConfig

/**
 * Keeps ALL duplicated templates in one place.
 * Strategy-specific templates call these with different Paths.
 */
object FeatureTemplates {

    data class Paths(
        val domainModelImport: (featureSnake: String) -> String,
        val domainRepoImport: (featureSnake: String) -> String,
        val dataModelImport: (featureSnake: String) -> String,
        val dataRepoImport: (featureSnake: String) -> String,
        val dataRemoteImport: (featureSnake: String) -> String,
        val useCaseImportRepo: (featureSnake: String) -> String,
        val useCaseImportModel: (featureSnake: String) -> String,
    )

    // ---- Shared (same content; only imports vary) ----

    fun domainModel(featurePascal: String) = """
      class $featurePascal {
        final int id;
        final String name;

        const $featurePascal({
          required this.id,
          required this.name,
        });
      }
    """.trimIndent()

    fun domainRepository(featurePascal: String, featureSnake: String, paths: Paths) = """
      import '${paths.domainModelImport(featureSnake)}';

      abstract class ${featurePascal}Repository {
        Future<List<$featurePascal>> get${featurePascal}List();
      }
    """.trimIndent()

    fun useCase(featurePascal: String, featureSnake: String, paths: Paths) = """
      import '${paths.useCaseImportRepo(featureSnake)}';
      import '${paths.useCaseImportModel(featureSnake)}';

      class Get${featurePascal}ListUseCase {
        final ${featurePascal}Repository repository;

        Get${featurePascal}ListUseCase(this.repository);

        Future<List<$featurePascal>> call() {
          return repository.get${featurePascal}List();
        }
      }
    """.trimIndent()

    fun apiService(featurePascal: String, featureSnake: String, paths: Paths) = """
      import 'package:dio/dio.dart';
      import '${paths.dataModelImport(featureSnake)}';

      class ${featurePascal}ApiService {
        final Dio _dio;

        ${featurePascal}ApiService(this._dio);

        Future<List<$featurePascal>> get${featurePascal}List() async {
          final response = await _dio.get('/$featureSnake');
          // TODO: parse response
          return [];
        }
      }
    """.trimIndent()

    fun repositoryImpl(featurePascal: String, featureSnake: String, paths: Paths) = """
      import '${paths.dataRepoImport(featureSnake)}';
      import '${paths.dataModelImport(featureSnake)}';
      import '${paths.dataRemoteImport(featureSnake)}';

      class ${featurePascal}RepositoryImpl implements ${featurePascal}Repository {
        final ${featurePascal}ApiService api;

        ${featurePascal}RepositoryImpl(this.api);

        @override
        Future<List<$featurePascal>> get${featurePascal}List() async {
          return api.get${featurePascal}List();
        }
      }
    """.trimIndent()

    fun simpleListState(featurePascal: String, featureSnake: String, paths: Paths) = """
      import '${paths.domainModelImport(featureSnake)}';

      class ${featurePascal}State {
        final bool loading;
        final String? error;
        final List<$featurePascal> items;

        const ${featurePascal}State({
          this.loading = false,
          this.error,
          this.items = const [],
        });

        ${featurePascal}State copyWith({
          bool? loading,
          String? error,
          List<$featurePascal>? items,
        }) {
          return ${featurePascal}State(
            loading: loading ?? this.loading,
            error: error,
            items: items ?? this.items,
          );
        }
      }
    """.trimIndent()

    // ---- Shared DI snippet ----

    fun useCaseInjection(featurePascal: String, di: CleanArchitectureConfig.DependencyInjection): String {
        val useCaseType = "Get${featurePascal}ListUseCase"
        return when (di) {
            CleanArchitectureConfig.DependencyInjection.GET_IT ->
                "GetIt.I.get<$useCaseType>(),"
            else ->
                "// TODO: provide $useCaseType here (no DI selected)\n              throw UnimplementedError(),"
        }
    }

    fun getItImport(di: CleanArchitectureConfig.DependencyInjection): String =
        if (di == CleanArchitectureConfig.DependencyInjection.GET_IT) "import 'package:get_it/get_it.dart';" else ""
}
