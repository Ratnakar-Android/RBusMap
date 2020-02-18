package com.github.component

import com.bus.map.demo.module.OSRMApi
import dagger.Component

@Component(modules = [GitHubApiModule::class])
interface GitHubAppComponent {
    fun getGitHubApi(): OSRMApi
}