package com.example.demo.flutter.mvvm.riverpod


import com.example.demo.CleanArchitectureConfig

object RiverpodTemplates {

    fun domainModel(featurePascal: String, featureSnake: String) = """
      class $featurePascal {
        final int id;
        final String name;

        const $featurePascal({
          required this.id,
          required this.name,
        });
      }
    """.trimIndent()

    fun domainRepository(featurePascal: String, featureSnake: String) = """
      import '${featureSnake}_model.dart';

      abstract class ${featurePascal}Repository {
        Future<List<$featurePascal>> get${featurePascal}List();
      }
    """.trimIndent()

    fun useCase(featurePascal: String, featureSnake: String) = """
      import '../repository/${featureSnake}_repository.dart';
      import '../model/${featureSnake}_model.dart';

      class Get${featurePascal}ListUseCase {
        final ${featurePascal}Repository repository;

        Get${featurePascal}ListUseCase(this.repository);

        Future<List<$featurePascal>> call() {
          return repository.get${featurePascal}List();
        }
      }
    """.trimIndent()

    fun apiService(featurePascal: String, featureSnake: String) = """
      import 'package:dio/dio.dart';
      import '../../domain/model/${featureSnake}_model.dart';

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

    fun repositoryImpl(featurePascal: String, featureSnake: String) = """
      import '../../domain/repository/${featureSnake}_repository.dart';
      import '../remote/${featureSnake}_api_service.dart';
      import '../../domain/model/${featureSnake}_model.dart';

      class ${featurePascal}RepositoryImpl implements ${featurePascal}Repository {
        final ${featurePascal}ApiService api;

        ${featurePascal}RepositoryImpl(this.api);

        @override
        Future<List<$featurePascal>> get${featurePascal}List() async {
          return api.get${featurePascal}List();
        }
      }
    """.trimIndent()

    fun viewModel(featurePascal: String, featureSnake: String) = """
      import 'package:flutter_riverpod/flutter_riverpod.dart';
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '../../domain/model/${featureSnake}_model.dart';

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

      class ${featurePascal}Notifier extends StateNotifier<${featurePascal}State> {
        final Get${featurePascal}ListUseCase _useCase;

        ${featurePascal}Notifier(this._useCase)
            : super(const ${featurePascal}State());

        Future<void> fetch() async {
          state = state.copyWith(loading: true);

          try {
            final result = await _useCase();
            state = state.copyWith(
              loading: false,
              items: result,
              error: null,
            );
          } catch (e) {
            state = state.copyWith(
              loading: false,
              error: e.toString(),
            );
          }
        }
      }
    """.trimIndent()

    fun providers(featurePascal: String, featureSnake: String, di: CleanArchitectureConfig.DependencyInjection) = """
      import 'package:flutter_riverpod/flutter_riverpod.dart';
      ${if (di == CleanArchitectureConfig.DependencyInjection.GET_IT)
        "import 'package:get_it/get_it.dart';"
    else
        ""
    }
      import '../../data/remote/${featureSnake}_api_service.dart';
      import '../../data/repository/${featureSnake}_repository_impl.dart';
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '${featureSnake}_state_notifier.dart';

      final ${featureSnake}NotifierProvider =
          StateNotifierProvider<${featurePascal}Notifier, ${featurePascal}State>(
        (ref) {
          ${if (di == CleanArchitectureConfig.DependencyInjection.GET_IT)
        "final useCase = GetIt.I.get<Get${featurePascal}ListUseCase>();"
    else
        "// TODO: provide Get${featurePascal}ListUseCase manually\n          throw UnimplementedError();"
    }
          return ${featurePascal}Notifier(useCase);
        },
      );
    """.trimIndent()

    fun screen(featurePascal: String, featureSnake: String) = """
      import 'package:flutter/material.dart';
      import 'package:flutter_riverpod/flutter_riverpod.dart';
      import '../viewmodel/${featureSnake}_providers.dart';

      class ${featurePascal}Screen extends ConsumerWidget {
        const ${featurePascal}Screen({super.key});

        @override
        Widget build(BuildContext context, WidgetRef ref) {
          final state = ref.watch(${featureSnake}NotifierProvider);

          if (state.loading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (state.error != null) {
            return Center(child: Text(state.error!));
          }

          return ListView.builder(
            itemCount: state.items.length,
            itemBuilder: (context, index) {
              final item = state.items[index];
              return ListTile(
                title: Text(item.name),
              );
            },
          );
        }
      }
    """.trimIndent()
}
