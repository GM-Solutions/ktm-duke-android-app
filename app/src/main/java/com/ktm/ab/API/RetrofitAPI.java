package com.ktm.ab.API;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ktm.ab.Util.Constants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Nikhil on 18-05-2016.
 */
public class RetrofitAPI {
    private static RetrofitAPI _me;
    private static RestAdapter restAdapter;

    private RetrofitAPI() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.Host)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(okHttpClient))
//                .setRequestInterceptor(new SessionRequestInterceptor())
                .build();
    }


    public static RetrofitAPIInterface getApi() {
        if (_me == null) {
            getInstance();
        }
        return restAdapter.create(RetrofitAPIInterface.class);
    }

    public static RetrofitAPI getInstance() {
        if (_me == null) {
            synchronized (RetrofitAPI.class) {
                if (_me == null) {
                    _me = new RetrofitAPI();
                }
            }
        }
        return _me;
    }
}
