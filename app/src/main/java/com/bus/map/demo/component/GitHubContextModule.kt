package com.github.component

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class GitHubContextModule(var mContext: Context) {


    @Provides
    fun getContext(): Context {
        return mContext
    }
}