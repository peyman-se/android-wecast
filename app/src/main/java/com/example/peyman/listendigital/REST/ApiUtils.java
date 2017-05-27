package com.example.peyman.listendigital.REST;

/**
 * Created by Peyman on 5/26/17.
 */
public class ApiUtils {

    public static final String BASE_URL = "http://10.0.2.2:8000/";

    public static Calls callServer() {
        return RetrofitClient.getClient(BASE_URL).create(Calls.class);
    }
}
