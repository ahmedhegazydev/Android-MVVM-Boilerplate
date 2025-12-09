package com.example.demo.java.mvvm.dagger

object JavaFeatureTemplates {

    // ============ DOMAIN ============

    fun domainModel(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.domain.model;

            // TODO: عدّل الفيلدز حسب الدومين الحقيقي
            public class $featurePascal {

                private final long id;
                private final String name;

                public $featurePascal(long id, String name) {
                    this.id = id;
                    this.name = name;
                }

                public long getId() {
                    return id;
                }

                public String getName() {
                    return name;
                }
            }
        """.trimIndent()
    }

    fun domainRepository(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.domain.repository;

            import java.util.List;

            import core.common.Resource;
            import features.$featureLower.domain.model.$featurePascal;

            public interface ${featurePascal}Repository {

                // TODO: غيّر الـ return type حسب استخدامك (Flow / LiveData / Rx...)
                Resource<List<$featurePascal>> get${featurePascal}List();
            }
        """.trimIndent()
    }

    fun useCase(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.domain.usecase;

            import java.util.List;

            import core.common.Resource;
            import features.$featureLower.domain.model.$featurePascal;
            import features.$featureLower.domain.repository.${featurePascal}Repository;

            public class Get${featurePascal}ListUseCase {

                private final ${featurePascal}Repository repository;

                public Get${featurePascal}ListUseCase(${featurePascal}Repository repository) {
                    this.repository = repository;
                }

                public Resource<List<$featurePascal>> execute() {
                    // TODO: ضيف أي لوجيك إضافي قبل/بعد النداء على الريبو
                    return repository.get${featurePascal}List();
                }
            }
        """.trimIndent()
    }

    // ============ DATA (REMOTE) ============

    fun dto(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.data.remote.dto;

            // TODO: عدّل الفيلدز لتطابق الـ API response
            public class ${featurePascal}Dto {

                private long id;
                private String name;

                public long getId() {
                    return id;
                }

                public void setId(long id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        """.trimIndent()
    }

    fun apiService(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.data.remote;

            import java.util.List;

            import features.$featureLower.data.remote.dto.${featurePascal}Dto;
            import retrofit2.Call;
            import retrofit2.http.GET;

            public interface ${featurePascal}ApiService {

                // TODO: عدّل الـ endpoint
                @GET("api/$featureLower")
                Call<List<${featurePascal}Dto>> get${featurePascal}List();
            }
        """.trimIndent()
    }

    // ============ DATA (LOCAL) ============

    fun entity(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.data.local.entity;

            import androidx.room.Entity;
            import androidx.room.PrimaryKey;

            @Entity(tableName = "${featureLower}_table")
            public class ${featurePascal}Entity {

                @PrimaryKey(autoGenerate = true)
                private long id;

                private String name;

                public ${featurePascal}Entity(String name) {
                    this.name = name;
                }

                public long getId() {
                    return id;
                }

                public void setId(long id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        """.trimIndent()
    }

    fun dao(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.data.local.dao;

            import java.util.List;

            import androidx.room.Dao;
            import androidx.room.Insert;
            import androidx.room.OnConflictStrategy;
            import androidx.room.Query;
            import features.$featureLower.data.local.entity.${featurePascal}Entity;

            @Dao
            public interface ${featurePascal}Dao {

                @Query("SELECT * FROM ${featureLower}_table")
                List<${featurePascal}Entity> getAll();

                @Insert(onConflict = OnConflictStrategy.REPLACE)
                void insertAll(List<${featurePascal}Entity> items);

                @Query("DELETE FROM ${featureLower}_table")
                void clear();
            }
        """.trimIndent()
    }

    fun repositoryImpl(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.data.repository;

            import java.io.IOException;
            import java.util.ArrayList;
            import java.util.List;

            import core.common.ErrorHandler;
            import core.common.Resource;
            import core.database.AppDatabase;
            import features.$featureLower.data.local.dao.${featurePascal}Dao;
            import features.$featureLower.data.local.entity.${featurePascal}Entity;
            import features.$featureLower.data.remote.${featurePascal}ApiService;
            import features.$featureLower.data.remote.dto.${featurePascal}Dto;
            import features.$featureLower.domain.model.$featurePascal;
            import features.$featureLower.domain.repository.${featurePascal}Repository;
            import retrofit2.Response;

            public class ${featurePascal}RepositoryImpl implements ${featurePascal}Repository {

                private final AppDatabase db;
                private final ${featurePascal}ApiService api;

                public ${featurePascal}RepositoryImpl(AppDatabase db,
                                                      ${featurePascal}ApiService api) {
                    this.db = db;
                    this.api = api;
                }

                @Override
                public Resource<List<$featurePascal>> get${featurePascal}List() {
                    try {
                        ${featurePascal}Dao dao = db.${featureLower}Dao();

                        // Load from network
                        Response<List<${featurePascal}Dto>> response = api.get${featurePascal}List().execute();
                        if (response.isSuccessful() && response.body() != null) {
                            List<${featurePascal}Dto> dtos = response.body();

                            List<${featurePascal}Entity> entities = new ArrayList<>();
                            for (${featurePascal}Dto dto : dtos) {
                                ${featurePascal}Entity entity =
                                        new ${featurePascal}Entity(dto.getName());
                                entities.add(entity);
                            }

                            dao.clear();
                            dao.insertAll(entities);
                        }

                        List<${featurePascal}Entity> local = dao.getAll();
                        List<$featurePascal> domainList = new ArrayList<>();
                        for (${featurePascal}Entity entity : local) {
                            $featurePascal item = new $featurePascal(
                                    entity.getId(),
                                    entity.getName()
                            );
                            domainList.add(item);
                        }

                        return new Resource.Success<>(domainList);

                    } catch (IOException e) {
                        return new Resource.Error(
                                ErrorHandler.getErrorMessage(e),
                                e
                        );
                    } catch (Throwable t) {
                        return new Resource.Error(
                                ErrorHandler.getErrorMessage(t),
                                t
                        );
                    }
                }
            }
        """.trimIndent()
    }

    // ============ PRESENTATION (STATE) ============

    fun uiState(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.presentation.state;

            import java.util.List;

            import features.$featureLower.domain.model.$featurePascal;

            public class ${featurePascal}UiState {

                private final boolean loading;
                private final String errorMessage;
                private final List<$featurePascal> items;

                public ${featurePascal}UiState(boolean loading,
                                               String errorMessage,
                                               List<$featurePascal> items) {
                    this.loading = loading;
                    this.errorMessage = errorMessage;
                    this.items = items;
                }

                public static ${featurePascal}UiState loading() {
                    return new ${featurePascal}UiState(true, null, null);
                }

                public static ${featurePascal}UiState error(String message) {
                    return new ${featurePascal}UiState(false, message, null);
                }

                public static ${featurePascal}UiState success(List<$featurePascal> items) {
                    return new ${featurePascal}UiState(false, null, items);
                }

                public boolean isLoading() {
                    return loading;
                }

                public String getErrorMessage() {
                    return errorMessage;
                }

                public List<$featurePascal> getItems() {
                    return items;
                }
            }
        """.trimIndent()
    }

    // ============ PRESENTATION (VIEWMODEL) ============

    fun viewModel(featurePascal: String): String {
        val featureLower = featurePascal.lowercase()
        return """
            package features.$featureLower.presentation.viewmodel;

            import androidx.lifecycle.LiveData;
            import androidx.lifecycle.MutableLiveData;
            import androidx.lifecycle.ViewModel;

            import java.util.List;

            import core.common.Resource;
            import features.$featureLower.domain.model.$featurePascal;
            import features.$featureLower.domain.usecase.Get${featurePascal}ListUseCase;
            import features.$featureLower.presentation.state.${featurePascal}UiState;

            // NOTE: لو حابب تستخدم Dagger للـ ViewModelFactory تقدر تعدّل الكونستركتور
            public class ${featurePascal}ViewModel extends ViewModel {

                private final Get${featurePascal}ListUseCase get${featurePascal}ListUseCase;

                private final MutableLiveData<${featurePascal}UiState> _uiState =
                        new MutableLiveData<>();
                public LiveData<${featurePascal}UiState> uiState = _uiState;

                public ${featurePascal}ViewModel(Get${featurePascal}ListUseCase get${featurePascal}ListUseCase) {
                    this.get${featurePascal}ListUseCase = get${featurePascal}ListUseCase;
                    loadData();
                }

                public void loadData() {
                    _uiState.setValue(${featurePascal}UiState.loading());

                    // مبدئياً Sync call – لو هتستخدم Coroutines / Executor عدّل هنا
                    Resource<List<$featurePascal>> result = get${featurePascal}ListUseCase.execute();

                    if (result instanceof Resource.Success) {
                        List<$featurePascal> data = ((Resource.Success<List<$featurePascal>>) result).getData();
                        _uiState.setValue(${featurePascal}UiState.success(data));
                    } else if (result instanceof Resource.Error) {
                        String msg = ((Resource.Error) result).getMessage();
                        _uiState.setValue(${featurePascal}UiState.error(msg));
                    }
                }
            }
        """.trimIndent()
    }

    // ============ PRESENTATION (FRAGMENT) ============

    fun fragment(featurePascal: String, featureSnake: String): String {
        val featureLower = featurePascal.lowercase()
        val layoutName = "fragment_${featureSnake}"
        val bindingClass = layoutName.split('_').joinToString("") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } + "Binding"
        return """
            package features.$featureLower.presentation.ui;

            import android.os.Bundle;
            import android.view.LayoutInflater;
            import android.view.View;
            import android.view.ViewGroup;

            import androidx.annotation.NonNull;
            import androidx.annotation.Nullable;
            import androidx.lifecycle.Observer;
            import androidx.lifecycle.ViewModelProvider;

            import core.common.BaseFragment;
            import features.$featureLower.presentation.state.${featurePascal}UiState;
            import features.$featureLower.presentation.viewmodel.${featurePascal}ViewModel;
            import $bindingClass; // TODO: تأكد من الباكدج الصحيح للـ ViewBinding

            public class ${featurePascal}Fragment extends BaseFragment<$bindingClass> {

                private ${featurePascal}ViewModel viewModel;

                @Override
                protected $bindingClass inflateBinding(@NonNull LayoutInflater inflater,
                                                       @Nullable ViewGroup container,
                                                       boolean attachToParent) {
                    return $bindingClass.inflate(inflater, container, attachToParent);
                }

                @Override
                public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                    super.onViewCreated(view, savedInstanceState);

                    viewModel = new ViewModelProvider(this).get(${featurePascal}ViewModel.class);

                    viewModel.uiState.observe(
                            getViewLifecycleOwner(),
                            new Observer<${featurePascal}UiState>() {
                                @Override
                                public void onChanged(${featurePascal}UiState state) {
                                    renderState(state);
                                }
                            }
                    );
                }

                private void renderState(${featurePascal}UiState state) {
                    // TODO: اربط الـ state بالـ UI
                    // مثال:
                    // getBinding().progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
                }
            }
        """.trimIndent()
    }
}
