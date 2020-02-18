package com.github.component

import com.bus.map.demo.module.OSRMApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module(includes = [GitHubRetrofitBuilderModule::class])
class GitHubApiModule {

    @Provides
    fun getGitHubApi(builder : Retrofit.Builder): OSRMApi {
        var sGitHubApi: OSRMApi =  builder.build().create(OSRMApi::class.java)
        return sGitHubApi
    }
}