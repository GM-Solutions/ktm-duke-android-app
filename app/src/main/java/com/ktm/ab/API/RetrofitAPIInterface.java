package com.ktm.ab.API;

import com.google.gson.JsonObject;
import com.ktm.ab.model.UserInfo;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;


/**
 * Created by Nikhil on 18-05-2016.
 */
public interface RetrofitAPIInterface {


    @POST("/v1/consumers/ktm-product-details/")
    public void signIn(@Query("access_token") String token, @Body JsonObject loginDetails, Callback<UserInfo> jsonObjectCallback);


}