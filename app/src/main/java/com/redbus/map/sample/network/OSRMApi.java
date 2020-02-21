package com.redbus.map.sample.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface OSRMApi {
    @GET
    Call<JsonObject> getOSRMRouteData(@Url String url);
}
