package com.example.demo.java.mvvm.dagger


object JavaDaggerTemplates {

    val networkModule = """
        package core.di;

        import java.util.concurrent.TimeUnit;

        import javax.inject.Singleton;

        import core.utils.Constants;
        import dagger.Module;
        import dagger.Provides;
        import okhttp3.OkHttpClient;
        import okhttp3.logging.HttpLoggingInterceptor;
        import retrofit2.Retrofit;
        import retrofit2.converter.moshi.MoshiConverterFactory;

        @Module
        public class NetworkModule {

            @Provides
            @Singleton
            OkHttpClient provideOkHttpClient() {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.level(HttpLoggingInterceptor.Level.BODY);

                return new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .connectTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .readTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .writeTimeout(Constants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .build();
            }

            @Provides
            @Singleton
            Retrofit provideRetrofit(OkHttpClient client) {
                return new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .client(client)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build();
            }
        }
    """

    val databaseModule = """
        package core.di;

        import android.app.Application;

        import javax.inject.Singleton;

        import androidx.room.Room;
        import core.database.AppDatabase;
        import core.utils.Constants;
        import dagger.Module;
        import dagger.Provides;

        @Module
        public class DatabaseModule {

            @Provides
            @Singleton
            AppDatabase provideDatabase(Application application) {
                return Room.databaseBuilder(
                        application,
                        AppDatabase.class,
                        Constants.DB_NAME
                ).build();
            }
        }
    """

    val dispatcherModule = """
        package core.di;

        import javax.inject.Singleton;

        import core.common.DefaultDispatcherProvider;
        import core.common.DispatcherProvider;
        import dagger.Module;
        import dagger.Provides;

        @Module
        public class DispatcherModule {

            @Provides
            @Singleton
            DispatcherProvider provideDispatcherProvider() {
                return new DefaultDispatcherProvider();
            }
        }
    """

    fun featureModule(featurePascal: String) = """
        package features.${featurePascal.lowercase()}.di;

        import javax.inject.Singleton;

        import core.common.DispatcherProvider;
        import core.database.AppDatabase;
        import dagger.Module;
        import dagger.Provides;
        import retrofit2.Retrofit;
        import features.${featurePascal.lowercase()}.data.remote.${featurePascal}ApiService;
        import features.${featurePascal.lowercase()}.data.repository.${featurePascal}RepositoryImpl;
        import features.${featurePascal.lowercase()}.domain.repository.${featurePascal}Repository;

        @Module
        public class ${featurePascal}Module {

            @Provides
            @Singleton
            ${featurePascal}ApiService provide${featurePascal}Api(Retrofit retrofit) {
                return retrofit.create(${featurePascal}ApiService.class);
            }

            @Provides
            @Singleton
            ${featurePascal}Repository provide${featurePascal}Repository(
                    AppDatabase db,
                    ${featurePascal}ApiService api,
                    DispatcherProvider dispatcherProvider
            ) {
                return new ${featurePascal}RepositoryImpl(db, api, dispatcherProvider);
            }
        }
    """
}
