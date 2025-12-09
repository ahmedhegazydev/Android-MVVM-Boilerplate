package com.example.demo.java.mvvm.core

object JavaCoreTemplates {

    val resource = """
        package core.common;

        public abstract class Resource<T> {

            private Resource() {}

            public static final class Success<T> extends Resource<T> {
                private final T data;

                public Success(T data) {
                    this.data = data;
                }

                public T getData() {
                    return data;
                }
            }

            public static final class Error extends Resource<Object> {
                private final String message;
                private final Throwable throwable;

                public Error(String message, Throwable throwable) {
                    this.message = message;
                    this.throwable = throwable;
                }

                public String getMessage() {
                    return message;
                }

                public Throwable getThrowable() {
                    return throwable;
                }
            }

            public static final class Loading extends Resource<Object> {
                public static final Loading INSTANCE = new Loading();

                private Loading() {}
            }
        }
    """

    val baseViewModel = """
        package core.common;

        import androidx.lifecycle.ViewModel;
        import androidx.lifecycle.viewModelScope;
        import kotlinx.coroutines.CoroutineExceptionHandler;
        import kotlinx.coroutines.CoroutineScope;
        import kotlinx.coroutines.Job;
        import kotlinx.coroutines.launch;

        public abstract class BaseViewModel extends ViewModel {

            private final DispatcherProvider dispatcherProvider;
            private final CoroutineExceptionHandler errorHandler;

            public BaseViewModel(DispatcherProvider dispatcherProvider) {
                this.dispatcherProvider = dispatcherProvider;
                this.errorHandler = new CoroutineExceptionHandler() {
                    @Override
                    public void handleException(CoroutineContext context, Throwable throwable) {
                        onCoroutineError(throwable);
                    }
                };
            }

            protected void launchIo(kotlin.jvm.functions.Function2<CoroutineScope, Continuation<? super kotlin.Unit>, Object> block) {
                viewModelScope.launch(dispatcherProvider.getIo().plus(errorHandler), block);
            }

            protected void onCoroutineError(Throwable throwable) {
                // override if needed
            }
        }
    """

    val dispatcherProvider = """
        package core.common;

        import kotlinx.coroutines.CoroutineDispatcher;
        import kotlinx.coroutines.Dispatchers;

        public interface DispatcherProvider {
            CoroutineDispatcher getIo();
            CoroutineDispatcher getMain();
            CoroutineDispatcher getDefault();
        }

        public class DefaultDispatcherProvider implements DispatcherProvider {

            @Override
            public CoroutineDispatcher getIo() {
                return Dispatchers.getIO();
            }

            @Override
            public CoroutineDispatcher getMain() {
                return Dispatchers.getMain();
            }

            @Override
            public CoroutineDispatcher getDefault() {
                return Dispatchers.getDefault();
            }
        }
    """

    val baseFragment = """
        package core.common;

        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.viewbinding.ViewBinding;

        public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

            private VB binding;

            protected abstract VB inflateBinding(@NonNull LayoutInflater inflater,
                                                 @Nullable ViewGroup container,
                                                 boolean attachToParent);

            protected VB getBinding() {
                if (binding == null) {
                    throw new IllegalStateException("Binding accessed before onCreateView or after onDestroyView");
                }
                return binding;
            }

            @Nullable
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater,
                                     @Nullable ViewGroup container,
                                     @Nullable Bundle savedInstanceState) {
                binding = inflateBinding(inflater, container, false);
                return binding.getRoot();
            }

            @Override
            public void onDestroyView() {
                super.onDestroyView();
                binding = null;
            }
        }
    """

    val errorHandler = """
        package core.common;

        import java.io.IOException;

        import retrofit2.HttpException;

        public final class ErrorHandler {

            private ErrorHandler() {}

            public static String getErrorMessage(Throwable throwable) {
                if (throwable instanceof IOException) {
                    return "Network error, please check your connection.";
                } else if (throwable instanceof HttpException) {
                    HttpException http = (HttpException) throwable;
                    return "Server error: " + http.code();
                } else {
                    String msg = throwable.getLocalizedMessage();
                    return msg != null ? msg : "Unexpected error occurred.";
                }
            }
        }
    """

    val constants = """
        package core.utils;

        public final class Constants {

            private Constants() {}

            public static final String BASE_URL = "https://api.example.com/";
            public static final String DB_NAME = "app_database.db";
            public static final long NETWORK_TIMEOUT_SECONDS = 30L;
        }
    """

    val appDatabase = """
        package core.database;

        import androidx.room.Database;
        import androidx.room.RoomDatabase;

        @Database(
                entities = {},
                version = 1,
                exportSchema = false
        )
        public abstract class AppDatabase extends RoomDatabase {
            // TODO: add DAOs
        }
    """
}