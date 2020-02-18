package com.github.component

import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor

@Module
class GitHubLoginIntercepterModule {

    @Provides
    fun gelogginIntercepter(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


}