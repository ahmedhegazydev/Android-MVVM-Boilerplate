package com.example.demo.kotlin.mvvm.koin

object KoinDiTemplates {

    val networkModule = """
        package core.di

        import core.utils.Constants
        import okhttp3.OkHttpClient
        import okhttp3.logging.HttpLoggingInterceptor
        import org.koin.dsl.module
        import retrofit2.Retrofit
        import retrofit2.converter.moshi.MoshiConverterFactory
        import java.util.concurrent.TimeUnit

        val networkModule = module {

            single {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build()
            }

            single {
                Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(get())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            }
        }
    """

    val databaseModule = """
        package core.di

        import android.app.Application
        import androidx.room.Room
        import core.database.AppDatabase
        import core.utils.Constants
        import org.koin.android.ext.koin.androidApplication
        import org.koin.dsl.module

        val databaseModule = module {

            single<AppDatabase> {
                Room.databaseBuilder(
                    androidApplication(),
                    AppDatabase::class.java,
                    Constants.DB_NAME
                ).build()
            }
        }
    """

    val dispatcherModule = """
        package core.di

        import core.common.DefaultDispatcherProvider
        import core.common.DispatcherProvider
        import org.koin.dsl.module

        val dispatcherModule = module {

            single<DispatcherProvider> { DefaultDispatcherProvider() }
        }
    """

    fun featureModule(featurePascal: String) = """
        package features.${featurePascal.lowercase()}.di

        import core.database.AppDatabase
        import core.common.DispatcherProvider
        import org.koin.androidx.viewmodel.dsl.viewModel
        import org.koin.dsl.module
        import retrofit2.Retrofit
        import features.${featurePascal.lowercase()}.data.remote.${featurePascal}ApiService
        import features.${featurePascal.lowercase()}.data.repository.${featurePascal}RepositoryImpl
        import features.${featurePascal.lowercase()}.domain.repository.${featurePascal}Repository
        import features.${featurePascal.lowercase()}.presentation.viewmodel.${featurePascal}ViewModel

        val ${featurePascal.lowercase()}Module = module {

            single<${featurePascal}ApiService> {
                get<Retrofit>().create(${featurePascal}ApiService::class.java)
            }

            single<${featurePascal}Repository> {
                ${featurePascal}RepositoryImpl(
                    db = get<AppDatabase>(),
                    api = get<${featurePascal}ApiService>(),
                    dispatcherProvider = get<DispatcherProvider>()
                )
            }

            viewModel {
                ${featurePascal}ViewModel(
                    dispatcherProvider = get(),
                    get${featurePascal}ListUseCase = get()
                )
            }
        }
    """
}
