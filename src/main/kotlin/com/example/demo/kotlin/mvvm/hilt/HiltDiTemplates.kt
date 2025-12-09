package com.example.demo.kotlin.mvvm.hilt

object HiltDiTemplates {

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

    fun featureModule(featurePascal: String) = """
        package features.${featurePascal.lowercase()}.di

        import core.database.AppDatabase
        import core.common.DispatcherProvider
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import retrofit2.Retrofit
        import javax.inject.Singleton
        import features.${featurePascal.lowercase()}.data.remote.${featurePascal}ApiService
        import features.${featurePascal.lowercase()}.data.repository.${featurePascal}RepositoryImpl
        import features.${featurePascal.lowercase()}.domain.repository.${featurePascal}Repository

        @Module
        @InstallIn(SingletonComponent::class)
        object ${featurePascal}Module {

            @Provides
            @Singleton
            fun provide${featurePascal}Api(retrofit: Retrofit): ${featurePascal}ApiService =
                retrofit.create(${featurePascal}ApiService::class.java)

            @Provides
            @Singleton
            fun provide${featurePascal}Repository(
                db: AppDatabase,
                api: ${featurePascal}ApiService,
                dispatcherProvider: DispatcherProvider
            ): ${featurePascal}Repository =
                ${featurePascal}RepositoryImpl(db, api, dispatcherProvider)
        }
    """
}
