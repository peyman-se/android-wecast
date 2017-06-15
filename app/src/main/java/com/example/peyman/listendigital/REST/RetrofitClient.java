package com.example.peyman.listendigital.REST;

/**
 * Created by Peyman on 5/26/17.
 */

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl, final String token) {
//        SharedPreferences prefs = getSharedPreferences("token", Context.MODE_PRIVATE)
        if (retrofit==null) {
            Log.i("peyman", "token in retrofit client is " + token);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // add your other interceptors …
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {

                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", token).build();
                    return chain.proceed(request);
                }
            });

            // add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
