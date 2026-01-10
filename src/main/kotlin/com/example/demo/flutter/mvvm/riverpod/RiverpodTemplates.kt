package com.example.demo.flutter.mvvm.riverpod

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.shared.FeaturePathProfiles
import com.example.demo.flutter.mvvm.shared.FeatureTemplates

object RiverpodTemplates {

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

    // Riverpod-only: Notifier + State (ممكن كمان تخليه shared لو عايز)
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

        ${featurePascal}Notifier(this._useCase) : super(const ${featurePascal}State());

        Future<void> fetch() async {
          state = state.copyWith(loading: true, error: null);

          try {
            final result = await _useCase();
            state = state.copyWith(loading: false, items: result, error: null);
          } catch (e) {
            state = state.copyWith(loading: false, error: e.toString());
          }
        }
      }
    """.trimIndent()

    fun providers(featurePascal: String, featureSnake: String, di: CleanArchitectureConfig.DependencyInjection) = """
      import 'package:flutter_riverpod/flutter_riverpod.dart';
      ${FeatureTemplates.getItImport(di)}
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '${featureSnake}_state_notifier.dart';

      final ${featureSnake}NotifierProvider =
          StateNotifierProvider<${featurePascal}Notifier, ${featurePascal}State>((ref) {
        ${if (di == CleanArchitectureConfig.DependencyInjection.GET_IT)
        "final useCase = GetIt.I.get<Get${featurePascal}ListUseCase>();"
    else
        "// TODO: provide Get${featurePascal}ListUseCase manually\n        throw UnimplementedError();"
    }
        return ${featurePascal}Notifier(useCase);
      });
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
              return ListTile(title: Text(item.name));
            },
          );
        }
      }
    """.trimIndent()
}
