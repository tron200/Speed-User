package com.speed.user.models;

import com.speed.user.BuildConfig;
import com.speed.user.helper.URLHelper;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(URLHelper.BASE)
                    .addConverterFactory(GsonConverterFactory
                            .create(new GsonBuilder()
                                    .setLenient()
                                    .serializeNulls()
                                    .create()));

    private static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.connectTimeout(DefaultConstants.DEFAULT_CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.readTimeout(DefaultConstants.DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(DefaultConstants.DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
        }
        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }


}
