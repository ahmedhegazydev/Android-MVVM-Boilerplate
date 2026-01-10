package com.example.demo.flutter.mvvm.bloc

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.shared.FeaturePathProfiles
import com.example.demo.flutter.mvvm.shared.FeatureTemplates

object BlocTemplates {

    private val paths = FeaturePathProfiles.MVVM

    fun domainModel(featurePascal: String, featureSnake: String) =
        FeatureTemplates.domainModel(featurePascal)

    fun domainRepository(featurePascal: String, featureSnake: String) =
        FeatureTemplates.domainRepository(featurePascal, featureSnake, paths)

    fun useCase(featurePascal: String, featureSnake: String) =
        FeatureTemplates.useCase(featurePascal, featureSnake, paths)

    fun apiService(featurePascal: String, featureSnake: String) =
        FeatureTemplates.apiService(featurePascal, featureSnake, paths)

    fun repositoryImpl(featurePascal: String, featureSnake: String) =
        FeatureTemplates.repositoryImpl(featurePascal, featureSnake, paths)

    // bloc-only
    fun event(featurePascal: String) = """
      abstract class ${featurePascal}Event {
        const ${featurePascal}Event();
      }

      class ${featurePascal}FetchRequested extends ${featurePascal}Event {
        const ${featurePascal}FetchRequested();
      }
    """.trimIndent()

    fun state(featurePascal: String, featureSnake: String) =
        FeatureTemplates.simpleListState(featurePascal, featureSnake, paths)

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
      ${FeatureTemplates.getItImport(di)}
      import '../viewmodel/${featureSnake}_bloc.dart';
      import '../viewmodel/${featureSnake}_event.dart';
      import '../viewmodel/${featureSnake}_state.dart';

      class ${featurePascal}Screen extends StatelessWidget {
        const ${featurePascal}Screen({super.key});

        @override
        Widget build(BuildContext context) {
          return BlocProvider(
            create: (_) => ${featurePascal}Bloc(
              ${FeatureTemplates.useCaseInjection(featurePascal, di)}
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
                    return ListTile(title: Text(item.name));
                  },
                );
              },
            ),
          );
        }
      }
    """.trimIndent()
}
