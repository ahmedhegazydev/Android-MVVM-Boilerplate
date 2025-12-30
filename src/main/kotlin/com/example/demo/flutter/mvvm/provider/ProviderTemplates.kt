package com.example.demo.flutter.mvvm.provider

object ProviderTemplates {

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
      import '${featureSnake}_repository.dart';
      import '${featureSnake}_model.dart';

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
      import '${featureSnake}_model.dart';

      class ${featurePascal}ApiService {
        final Dio _dio;

        ${featurePascal}ApiService(this._dio);

        Future<List<$featurePascal>> get${featurePascal}List() async {
          final response = await _dio.get('/$featureSnake');
          // TODO: parse JSON properly
          return [];
        }
      }
    """.trimIndent()

    fun repositoryImpl(featurePascal: String, featureSnake: String) = """
      import '${featureSnake}_repository.dart';
      import '${featureSnake}_api_service.dart';
      import '${featureSnake}_model.dart';

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
      import 'package:flutter/foundation.dart';
      import 'get_${featureSnake}_list_usecase.dart';
      import '${featureSnake}_model.dart';

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

    fun screen(featurePascal: String, featureSnake: String, featureCamel: String) = """
      import 'package:flutter/material.dart';
      import 'package:provider/provider.dart';
      import '${featureSnake}_view_model.dart';

      class ${featurePascal}Screen extends StatelessWidget {
        const ${featurePascal}Screen({super.key});

        @override
        Widget build(BuildContext context) {
          return ChangeNotifierProvider(
            create: (_) => ${featurePascal}ViewModel(
              // TODO: inject Get${featurePascal}ListUseCase here (GetIt, etc.)
              throw UnimplementedError(),
            ),
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
}
