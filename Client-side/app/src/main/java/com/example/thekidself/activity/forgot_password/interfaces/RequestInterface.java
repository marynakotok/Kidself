package com.example.thekidself.activity.forgot_password.interfaces;

import com.example.thekidself.activity.forgot_password.models.ServerRequest;
import com.example.thekidself.activity.forgot_password.models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("TheKidSelfPHP/ForgotPassword/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
