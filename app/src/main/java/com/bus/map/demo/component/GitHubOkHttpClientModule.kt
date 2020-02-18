package com.github.component

import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module (includes = [GitHubCacheModule::class, GitHubNetworkIntercepterModule::class, GitHubLoginIntercepterModule::class])
class GitHubOkHttpClientModule {

    @Provides
    fun getOkHttpClient(cache : Cache, networkInterceptor : Interceptor, loggingInterceptor : HttpLoggingInterceptor): OkHttpClient {
        // Building OkHttp client
        val client = OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(networkInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()


        return client
    }
}