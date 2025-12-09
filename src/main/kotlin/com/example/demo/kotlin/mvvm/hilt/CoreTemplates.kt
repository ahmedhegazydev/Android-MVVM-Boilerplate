package com.example.demo.kotlin.mvvm.hilt


object CoreTemplates {

    val resource = """
        package core.common

        sealed class Resource<out T> {
            data class Success<T>(val data: T) : Resource<T>()
            data class Error(
                val message: String,
                val throwable: Throwable? = null
            ) : Resource<Nothing>()

            object Loading : Resource<Nothing>()
        }
    """

    val dispatcherProvider = """
        package core.common

        import kotlinx.coroutines.CoroutineDispatcher
        import kotlinx.coroutines.Dispatchers

        interface DispatcherProvider {
            val io: CoroutineDispatcher
            val main: CoroutineDispatcher
            val default: CoroutineDispatcher
        }

        class DefaultDispatcherProvider : DispatcherProvider {
            override val io: CoroutineDispatcher = Dispatchers.IO
            override val main: CoroutineDispatcher = Dispatchers.Main
            override val default: CoroutineDispatcher = Dispatchers.Default
        }
    """

    val baseViewModel = """
        package core.common

        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import kotlinx.coroutines.CoroutineExceptionHandler
        import kotlinx.coroutines.launch

        abstract class BaseViewModel(
            private val dispatcherProvider: DispatcherProvider
        ) : ViewModel() {

            private val errorHandler = CoroutineExceptionHandler { _, throwable ->
                onCoroutineError(throwable)
            }

            protected fun launchIo(block: suspend () -> Unit) {
                viewModelScope.launch(dispatcherProvider.io + errorHandler) {
                    block()
                }
            }

            protected open fun onCoroutineError(throwable: Throwable) {
                // subclasses can override for global error handling
            }
        }
    """

    val baseFragment = """
        package core.common

        import android.os.Bundle
        import android.view.LayoutInflater
        import android.view.View
        import android.view.ViewGroup
        import androidx.fragment.app.Fragment
        import androidx.viewbinding.ViewBinding

        abstract class BaseFragment<VB : ViewBinding>(
            private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
        ) : Fragment() {

            private var _binding: VB? = null
            protected val binding: VB
                get() = _binding ?: error("Binding accessed before onCreateView or after onDestroyView")

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                _binding = bindingInflater(inflater, container, false)
                return binding.root
            }

            override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
            }
        }
    """

    val errorHandler = """
        package core.common

        import retrofit2.HttpException
        import java.io.IOException

        object ErrorHandler {

            fun getErrorMessage(throwable: Throwable): String {
                return when (throwable) {
                    is IOException -> "Network error, please check your connection."
                    is HttpException -> "Server error: ${'$'}{throwable.code()}"
                    else -> throwable.localizedMessage ?: "Unexpected error occurred."
                }
            }
        }
    """

    val constants = """
        package core.utils

        object Constants {
            const val BASE_URL = "https://api.example.com/"
            const val DB_NAME = "app_database.db"
            const val NETWORK_TIMEOUT_SECONDS = 30L
        }
    """

    val networkModule = """
        package core.di

        import core.utils.Constants
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import okhttp3.OkHttpClient
        import okhttp3.logging.HttpLoggingInterceptor
        import retrofit2.Retrofit
        import retrofit2.converter.moshi.MoshiConverterFactory
        import javax.inject.Singleton
        import java.util.concurrent.TimeUnit

        @Module
        @InstallIn(SingletonComponent::class)
        object NetworkModule {

            @Provides
            @Singleton
            fun provideOkHttpClient(): OkHttpClient {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                return OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build()
            }

            @Provides
            @Singleton
            fun provideRetrofit(client: OkHttpClient): Retrofit =
                Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
        }
    """

    val databaseModule = """
        package core.di

        import android.content.Context
        import androidx.room.Room
        import core.database.AppDatabase
        import core.utils.Constants
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.android.qualifiers.ApplicationContext
        import dagger.hilt.components.SingletonComponent
        import javax.inject.Singleton

        @Module
        @InstallIn(SingletonComponent::class)
        object DatabaseModule {

            @Provides
            @Singleton
            fun provideDatabase(
                @ApplicationContext context: Context
            ): AppDatabase =
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    Constants.DB_NAME
                ).build()
        }
    """

    val dispatcherModule = """
        package core.di

        import core.common.DefaultDispatcherProvider
        import core.common.DispatcherProvider
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import javax.inject.Singleton

        @Module
        @InstallIn(SingletonComponent::class)
        object DispatcherModule {

            @Provides
            @Singleton
            fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
        }
    """

    val appDatabase = """
        package core.database

        import androidx.room.Database
        import androidx.room.RoomDatabase

        @Database(
            entities = [],
            version = 1,
            exportSchema = false
        )
        abstract class AppDatabase : RoomDatabase() {
            // TODO: add DAOs, e.g.:
            // abstract fun featureNameDao(): FeatureNameDao
        }
    """
}
