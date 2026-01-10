package com.example.demo.flutter.mvvm.cubit

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.shared.FeaturePathProfiles
import com.example.demo.flutter.mvvm.shared.FeatureTemplates

object CubitTemplates {

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

    fun state(featurePascal: String, featureSnake: String) =
        FeatureTemplates.simpleListState(featurePascal, featureSnake, paths)

    fun cubit(featurePascal: String, featureSnake: String) = """
      import 'package:flutter_bloc/flutter_bloc.dart';
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '${featureSnake}_state.dart';

      class ${featurePascal}Cubit extends Cubit<${featurePascal}State> {
        final Get${featurePascal}ListUseCase _useCase;

        ${featurePascal}Cubit(this._useCase) : super(const ${featurePascal}State());

        Future<void> fetch() async {
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
      import '../viewmodel/${featureSnake}_cubit.dart';
      import '../viewmodel/${featureSnake}_state.dart';

      class ${featurePascal}Screen extends StatelessWidget {
        const ${featurePascal}Screen({super.key});

        @override
        Widget build(BuildContext context) {
          return BlocProvider(
            create: (_) => ${featurePascal}Cubit(
              ${FeatureTemplates.useCaseInjection(featurePascal, di)}
            )..fetch(),
            child: BlocBuilder<${featurePascal}Cubit, ${featurePascal}State>(
              builder: (context, state) {
                if (state.loading) return const Center(child: CircularProgressIndicator());
                if (state.error != null) return Center(child: Text(state.error!));

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
