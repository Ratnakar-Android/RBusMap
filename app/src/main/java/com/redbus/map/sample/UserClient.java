package com.redbus.map.sample;

import android.app.Application;
import android.content.Context;

import com.github.component.DaggerGitHubAppComponent;
import com.github.component.GitHubAppComponent;
import com.github.component.GitHubContextModule;
import com.redbus.map.sample.network.OSRMApi;


public class UserClient extends Application {

    public OSRMApi osrmApi;
    private GitHubAppComponent gitHubAppComponent;


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        gitHubAppComponent = DaggerGitHubAppComponent.builder().gitHubContextModule(new GitHubContextModule(this)).build();
        osrmApi = gitHubAppComponent.getGitHubApi();

    }
}
