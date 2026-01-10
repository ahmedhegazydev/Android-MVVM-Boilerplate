package com.example.demo.flutter.mvvm.provider

import com.example.demo.CleanArchitectureConfig
import com.example.demo.flutter.mvvm.shared.FeaturePathProfiles
import com.example.demo.flutter.mvvm.shared.FeatureTemplates

object ProviderTemplates {

    private val paths = FeaturePathProfiles.FLAT

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

    // Provider-only: ViewModel (ChangeNotifier)
    fun viewModel(featurePascal: String, featureSnake: String) = """
      import 'package:flutter/foundation.dart';
      import '../../domain/usecase/get_${featureSnake}_list_usecase.dart';
      import '../../domain/model/${featureSnake}_model.dart';

      class ${featurePascal}ViewModel extends ChangeNotifier {
        final Get${featurePascal}ListUseCase _useCase;

        ${featurePascal}ViewModel(this._useCase);

        bool loading = false;
        String? error;
        List<$featurePascal> items = [];

        Future<void> fetch() async {
          loading = true;
          notifyListeners();

          try {
            final result = await _useCase();
            items = result;
            error = null;
          } catch (e) {
            error = e.toString();
          }

          loading = false;
          notifyListeners();
        }
      }
    """.trimIndent()

    // Provider-only: Screen
    fun screen(
        featurePascal: String,
        featureSnake: String,
        featureCamel: String,
        di: CleanArchitectureConfig.DependencyInjection
    ) = """
      import 'package:flutter/material.dart';
      import 'package:provider/provider.dart';
      ${FeatureTemplates.getItImport(di)}
      import '../viewmodel/${featureSnake}_view_model.dart';

      class ${featurePascal}Screen extends StatelessWidget {
        const ${featurePascal}Screen({super.key});

        @override
        Widget build(BuildContext context) {
          return ChangeNotifierProvider(
            create: (_) => ${featurePascal}ViewModel(
              ${FeatureTemplates.useCaseInjection(featurePascal, di)}
            )..fetch(),
            child: Consumer<${featurePascal}ViewModel>(
              builder: (context, vm, _) {
                if (vm.loading) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (vm.error != null) {
                  return Center(child: Text(vm.error!));
                }

                return ListView.builder(
                  itemCount: vm.items.length,
                  itemBuilder: (context, index) {
                    final item = vm.items[index];
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
