package com.github.component

import com.redbus.map.sample.network.OSRMApi
import dagger.Component

@Component(modules = [GitHubApiModule::class])
interface GitHubAppComponent {
    fun getGitHubApi(): OSRMApi
}