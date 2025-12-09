package com.example.demo


object FeatureTemplates {

    fun domainModel(featureName: String) = """
        package features.${featureName.lowercase()}.domain.model

        data class $featureName(
            val id: Long,
            val title: String,
            val description: String
        )
    """

    fun domainRepository(featureName: String) = """
        package features.${featureName.lowercase()}.domain.repository

        import core.common.Resource
        import features.${featureName.lowercase()}.domain.model.$featureName
        import kotlinx.coroutines.flow.Flow

        interface ${featureName}Repository {
            fun get${featureName}List(): Flow<Resource<List<$featureName>>>
        }
    """

    fun useCase(featureName: String) = """
        package features.${featureName.lowercase()}.domain.usecase

        import core.common.Resource
        import features.${featureName.lowercase()}.domain.model.$featureName
        import features.${featureName.lowercase()}.domain.repository.${featureName}Repository
        import kotlinx.coroutines.flow.Flow
        import javax.inject.Inject

        class Get${featureName}ListUseCase @Inject constructor(
            private val repository: ${featureName}Repository
        ) {
            operator fun invoke(): Flow<Resource<List<$featureName>>> =
                repository.get${featureName}List()
        }
    """

    fun dto(featureName: String) = """
        package features.${featureName.lowercase()}.data.remote.dto

        import features.${featureName.lowercase()}.domain.model.$featureName

        data class ${featureName}Dto(
            val id: Long?,
            val title: String?,
            val description: String?
        ) {
            fun toDomain(): $featureName = $featureName(
                id = id ?: 0L,
                title = title.orEmpty(),
                description = description.orEmpty()
            )
        }
    """

    fun apiService(featureName: String) = """
        package features.${featureName.lowercase()}.data.remote

        import features.${featureName.lowercase()}.data.remote.dto.${featureName}Dto
        import retrofit2.http.GET

        interface ${featureName}ApiService {

            @GET("${featureName.lowercase()}")
            suspend fun get${featureName}List(): List<${featureName}Dto>
        }
    """

    fun entity(featureName: String) = """
        package features.${featureName.lowercase()}.data.local.entity

        import androidx.room.Entity
        import androidx.room.PrimaryKey
        import features.${featureName.lowercase()}.domain.model.$featureName

        @Entity(tableName = "${featureName.lowercase()}")
        data class ${featureName}Entity(
            @PrimaryKey val id: Long,
            val title: String,
            val description: String
        ) {
            fun toDomain(): $featureName = $featureName(
                id = id,
                title = title,
                description = description
            )

            companion object {
                fun fromDomain(item: $featureName): ${featureName}Entity =
                    ${featureName}Entity(
                        id = item.id,
                        title = item.title,
                        description = item.description
                    )
            }
        }
    """

    fun dao(featureName: String) = """
        package features.${featureName.lowercase()}.data.local.dao

        import androidx.room.Dao
        import androidx.room.Insert
        import androidx.room.OnConflictStrategy
        import androidx.room.Query
        import features.${featureName.lowercase()}.data.local.entity.${featureName}Entity

        @Dao
        interface ${featureName}Dao {

            @Query("SELECT * FROM ${featureName.lowercase()}")
            suspend fun getAll(): List<${featureName}Entity>

            @Insert(onConflict = OnConflictStrategy.REPLACE)
            suspend fun insertAll(items: List<${featureName}Entity>)

            @Query("DELETE FROM ${featureName.lowercase()}")
            suspend fun clearAll()
        }
    """

    fun repositoryImpl(featureName: String) = """
        package features.${featureName.lowercase()}.data.repository

        import core.common.ErrorHandler
        import core.common.Resource
        import features.${featureName.lowercase()}.data.local.dao.${featureName}Dao
        import features.${featureName.lowercase()}.data.local.entity.${featureName}Entity
        import features.${featureName.lowercase()}.data.remote.${featureName}ApiService
        import features.${featureName.lowercase()}.domain.model.$featureName
        import features.${featureName.lowercase()}.domain.repository.${featureName}Repository
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.flow
        import javax.inject.Inject

        class ${featureName}RepositoryImpl @Inject constructor(
            private val api: ${featureName}ApiService,
            private val dao: ${featureName}Dao
        ) : ${featureName}Repository {

            override fun get${featureName}List(): Flow<Resource<List<$featureName>>> = flow {
                emit(Resource.Loading)

                try {
                    val remote = api.get${featureName}List().map { it.toDomain() }

                    dao.clearAll()
                    dao.insertAll(remote.map { ${featureName}Entity.fromDomain(it) })

                    emit(Resource.Success(remote))
                } catch (e: Throwable) {
                    val cached = dao.getAll().map { it.toDomain() }

                    if (cached.isNotEmpty()) {
                        emit(Resource.Success(cached))
                    } else {
                        emit(Resource.Error(ErrorHandler.getErrorMessage(e), e))
                    }
                }
            }
        }
    """

    fun diModule(featureName: String) = """
        package features.${featureName.lowercase()}.di

        import core.database.AppDatabase
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import features.${featureName.lowercase()}.data.local.dao.${featureName}Dao
        import features.${featureName.lowercase()}.data.remote.${featureName}ApiService
        import features.${featureName.lowercase()}.data.repository.${featureName}RepositoryImpl
        import features.${featureName.lowercase()}.domain.repository.${featureName}Repository
        import retrofit2.Retrofit
        import javax.inject.Singleton

        @Module
        @InstallIn(SingletonComponent::class)
        object ${featureName}Module {

            @Provides
            @Singleton
            fun provide${featureName}Api(retrofit: Retrofit): ${featureName}ApiService =
                retrofit.create(${featureName}ApiService::class.java)

            @Provides
            @Singleton
            fun provide${featureName}Dao(db: AppDatabase): ${featureName}Dao =
                db.${featureName.replaceFirstChar { it.lowercase() }}Dao() // TODO: add this in AppDatabase

            @Provides
            @Singleton
            fun provide${featureName}Repository(
                api: ${featureName}ApiService,
                dao: ${featureName}Dao
            ): ${featureName}Repository = ${featureName}RepositoryImpl(api, dao)
        }
    """

    fun uiState(featureName: String) = """
        package features.${featureName.lowercase()}.presentation.state

        import features.${featureName.lowercase()}.domain.model.$featureName

        sealed class ${featureName}UiState {
            object Loading : ${featureName}UiState()
            data class Success(val items: List<$featureName>) : ${featureName}UiState()
            data class Error(val message: String) : ${featureName}UiState()
            object Empty : ${featureName}UiState()
        }
    """

    fun viewModel(featureName: String) = """
        package features.${featureName.lowercase()}.presentation.viewmodel

        import androidx.lifecycle.LiveData
        import androidx.lifecycle.MutableLiveData
        import core.common.BaseViewModel
        import core.common.DispatcherProvider
        import core.common.Resource
        import dagger.hilt.android.lifecycle.HiltViewModel
        import features.${featureName.lowercase()}.domain.usecase.Get${featureName}ListUseCase
        import features.${featureName.lowercase()}.presentation.state.${featureName}UiState
        import javax.inject.Inject

        @HiltViewModel
        class ${featureName}ViewModel @Inject constructor(
            private val get${featureName}ListUseCase: Get${featureName}ListUseCase,
            dispatcherProvider: DispatcherProvider
        ) : BaseViewModel(dispatcherProvider) {

            private val _uiState = MutableLiveData<${featureName}UiState>()
            val uiState: LiveData<${featureName}UiState> = _uiState

            fun load${featureName}List() {
                _uiState.value = ${featureName}UiState.Loading

                launchIo {
                    get${featureName}ListUseCase().collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                val items = result.data
                                _uiState.postValue(
                                    if (items.isEmpty()) ${featureName}UiState.Empty
                                    else ${featureName}UiState.Success(items)
                                )
                            }

                            is Resource.Error ->
                                _uiState.postValue(${featureName}UiState.Error(result.message))

                            is Resource.Loading ->
                                _uiState.postValue(${featureName}UiState.Loading)
                        }
                    }
                }
            }
        }
    """

    fun fragment(featureName: String, featureSnake: String) = """
        package features.${featureName.lowercase()}.presentation.ui

        import android.os.Bundle
        import android.view.View
        import androidx.fragment.app.viewModels
        import core.common.BaseFragment
        import dagger.hilt.android.AndroidEntryPoint
        import features.${featureName.lowercase()}.databinding.Fragment${featureName}Binding
        import features.${featureName.lowercase()}.presentation.state.${featureName}UiState
        import features.${featureName.lowercase()}.presentation.viewmodel.${featureName}ViewModel

        @AndroidEntryPoint
        class ${featureName}Fragment :
            BaseFragment<Fragment${featureName}Binding>(Fragment${featureName}Binding::inflate) {

            private val viewModel: ${featureName}ViewModel by viewModels()

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                observeUi()
                viewModel.load${featureName}List()
            }

            private fun observeUi() {
                viewModel.uiState.observe(viewLifecycleOwner) { state ->
                    when (state) {
                        is ${featureName}UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.errorGroup.visibility = View.GONE
                            binding.contentGroup.visibility = View.GONE
                        }
                        is ${featureName}UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorGroup.visibility = View.GONE
                            binding.contentGroup.visibility = View.VISIBLE
                            // TODO: update UI with state.items
                        }
                        is ${featureName}UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorGroup.visibility = View.VISIBLE
                            binding.contentGroup.visibility = View.GONE
                            binding.errorText.text = state.message
                        }
                        ${featureName}UiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.errorGroup.visibility = View.GONE
                            binding.contentGroup.visibility = View.VISIBLE
                            // TODO: show empty state
                        }
                    }
                }
            }
        }
    """
}
