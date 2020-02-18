package com.bus.map.demo.listener;

import com.google.gson.JsonObject;

public interface OSRMRouteListener {

    public void onResult(JsonObject result);
    public void onFailure(Throwable e);
}
