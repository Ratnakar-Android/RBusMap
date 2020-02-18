package com.github.component

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides

@Module
class GitHubGsonModule {

    @Provides
    fun getGson(gSonBuilder: GsonBuilder): Gson {

        return gSonBuilder.create()

    }

    @Provides
    fun getGsonBuilder(): GsonBuilder {

        return GsonBuilder()

    }
}