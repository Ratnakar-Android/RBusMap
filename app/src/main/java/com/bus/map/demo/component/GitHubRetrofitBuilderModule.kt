package com.github.component

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [GitHubOkHttpClientModule::class, GitHubGsonModule::class])
class GitHubRetrofitBuilderModule {

    private val Git_hub_URL = "https://routing"

    @Provides
    fun getRetrofitBuilder(okHhttpClient : OkHttpClient, gson: Gson): Retrofit.Builder {
        // Retrofit Builder
        val builder = Retrofit.Builder()
                .baseUrl(Git_hub_URL)
                .client(okHhttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
        return builder
    }
}