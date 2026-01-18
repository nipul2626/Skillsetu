package com.example.skilllsetujava.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // ⚠️⚠️⚠️ IMPORTANT: CHANGE THIS TO YOUR COMPUTER'S IP ADDRESS ⚠️⚠️⚠️
    //
    // YOUR DEVICE IP: 192.168.89.113 (this is your phone)
    // YOU NEED YOUR COMPUTER'S IP (where backend is running)
    //
    // Example: If your computer IP is 192.168.89.100, use:
    // private static final String BASE_URL = "http://192.168.89.100:8081/";
    //
    // TO FIND YOUR COMPUTER IP:
    // Windows: Open CMD, type 'ipconfig', look for IPv4 Address

    private static final String BASE_URL = "http://192.168.31.8:8081/";

    // Example (REPLACE WITH YOUR ACTUAL IP):
    // private static final String BASE_URL = "http://192.168.89.100:8081/";

    private static Retrofit retrofit;

    public static ApiService getApiService() {
        if (retrofit == null) {

            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configure OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}