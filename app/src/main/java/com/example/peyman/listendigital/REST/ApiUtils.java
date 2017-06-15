package com.example.peyman.listendigital.REST;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Peyman on 5/26/17.
 */
public class ApiUtils {

    public static final String BASE_URL = "http://10.0.2.2:8000/";
    public static SharedPreferences sharedPreferences = getSharedPreferences("token");

    public static Calls callAuthServer(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        return RetrofitClient.getClient(BASE_URL, token).create(Calls.class);
    }

    public Calls callGuestServer() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(Calls.class);
    }
}
