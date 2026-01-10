package com.example.demo.flutter.mvvm.bloc

import com.example.demo.CleanArchitectureConfig

object BlocTemplates {

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
      import '../model/${featureSnake}_model.dart';

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
      import '../../domain/model/${featureSnake}_model.dart';
      import '../remote/${featureSnake}_api_service.dart';

      class ${featurePascal}RepositoryImpl implements ${featurePascal}Repository {
        final ${featurePascal}ApiService api;

        ${featurePascal}RepositoryImpl(this.api);

        @override
        Future<List<$featurePascal>> get${featurePascal}List() async {
          return api.get${featurePascal}List();
        }
      }
    """.trimIndent()

    fun event(featurePascal: String) = """
      abstract class ${featurePascal}Event {
        const ${featurePascal}Event();
      }

      class ${featurePascal}FetchRequested extends ${featurePascal}Event {
        const ${featurePascal}FetchRequested();
      }
    """.trimIndent()

    fun state(featurePascal: String, featureSnake: String) = """
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
    """.trimIndent()

    fun bloc(featurePascal: String, featureSnake: String) = """
      import 'package:flutter_bloc/flutter_bloc.dart';
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '${featureSnake}_event.dart';
      import '${featureSnake}_state.dart';

      class ${featurePascal}Bloc extends Bloc<${featurePascal}Event, ${featurePascal}State> {
        final Get${featurePascal}ListUseCase _useCase;

        ${featurePascal}Bloc(this._useCase) : super(const ${featurePascal}State()) {
          on<${featurePascal}FetchRequested>(_onFetch);
        }

        Future<void> _onFetch(
          ${featurePascal}FetchRequested event,
          Emitter<${featurePascal}State> emit,
        ) async {
          emit(state.copyWith(loading: true, error: null));

          try {
            final result = await _useCase();
            emit(state.copyWith(loading: false, items: result, error: null));
          } catch (e) {
            emit(state.copyWith(loading: false, error: e.toString()));
          }
        }
      }
    """.trimIndent()

    fun screen(featurePascal: String, featureSnake: String, di: CleanArchitectureConfig.DependencyInjection) = """
      import 'package:flutter/material.dart';
      import 'package:flutter_bloc/flutter_bloc.dart';
      ${
        if (di == CleanArchitectureConfig.DependencyInjection.GET_IT)
            "import 'package:get_it/get_it.dart';"
        else
            ""
    }
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '../viewmodel/${featureSnake}_bloc.dart';
      import '../viewmodel/${featureSnake}_event.dart';
      import '../viewmodel/${featureSnake}_state.dart';

      class ${featurePascal}Screen extends StatelessWidget {
        const ${featurePascal}Screen({super.key});

        @override
        Widget build(BuildContext context) {
          return BlocProvider(
            create: (_) => ${featurePascal}Bloc(
              ${useCaseInjection(featurePascal, di)}
            )..add(const ${featurePascal}FetchRequested()),
            child: BlocBuilder<${featurePascal}Bloc, ${featurePascal}State>(
              builder: (context, state) {
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
              },
            ),
          );
        }
      }
    """.trimIndent()

    private fun useCaseInjection(featurePascal: String, di: CleanArchitectureConfig.DependencyInjection): String {
        val useCaseType = "Get${featurePascal}ListUseCase"
        return when (di) {
            CleanArchitectureConfig.DependencyInjection.GET_IT ->
                "GetIt.I.get<$useCaseType>(),"

            else ->
                "// TODO: provide $useCaseType here (no DI selected)\n              throw UnimplementedError(),"
        }
    }
}
