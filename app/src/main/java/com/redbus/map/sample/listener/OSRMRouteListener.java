package com.redbus.map.sample.listener;

import com.google.gson.JsonObject;

public interface OSRMRouteListener {

    public void onResult(JsonObject result);
    public void onFailure(Throwable e);
}
