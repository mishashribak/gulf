package ApiRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import Utility.CheckConnection;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RequestClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder() .connectTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .addInterceptor(new NetworkConnectionInterceptor()).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static class NetworkConnectionInterceptor implements Interceptor {

        public void isInternetAvailable(){
            CheckConnection.isNetworkAvailable();
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            isInternetAvailable();
            return chain.proceed(request);
        }
    }
}
