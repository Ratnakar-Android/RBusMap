package com.github.component

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache

@Module(includes = [GitHubContextModule::class])
class GitHubCacheModule {

    @Provides
    fun getCache(mContext: Context): Cache {

        val cache = Cache(mContext!!.getApplicationContext().getCacheDir(), 5 * 1024 * 1024)
        return cache
    }
}