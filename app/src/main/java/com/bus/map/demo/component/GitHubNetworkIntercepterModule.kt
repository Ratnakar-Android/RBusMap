package com.github.component

import dagger.Module
import dagger.Provides
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

@Module
class GitHubNetworkIntercepterModule {

    @Provides
    fun getNetworkIntercepter(): Interceptor {

        // Used for cache connection
        val networkInterceptor = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                // set max-age and max-stale properties for cache header
                val cacheControl = CacheControl.Builder()
                        .maxAge(1, TimeUnit.HOURS)
                        .maxStale(3, TimeUnit.DAYS)
                        .build()
                return chain.proceed(chain.request())
                        .newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", cacheControl.toString())
                        .build()
            }
        }
      return networkInterceptor

    }

}