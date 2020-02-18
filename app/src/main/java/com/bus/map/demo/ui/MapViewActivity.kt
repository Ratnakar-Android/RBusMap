package com.bus.map.demo.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bus.map.demo.R
import com.bus.map.demo.databinding.ActivityMainBinding
import com.bus.map.demo.module.RedBusMapProvider

class MapViewActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MapViewModel
    var mapProviderImpl: RedBusMapProvider = RedBusMapProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initViewModel()
        binding.viewModel = viewModel
        mapProviderImpl.addMapToView(binding.mapFragmentContainer.id)
        initObservers()

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        viewModel.init(this, mapProviderImpl)
    }

    private fun initObservers() {
        viewModel.networkCheckFailed?.observe(this, networkEventObseerver)
        viewModel.errorState?.observe(this, errorStateObserver)
    }

    val errorStateObserver = Observer<String>{message ->
        displayErrorToast(message)
    }
    val networkEventObseerver = Observer<Boolean> { vt ->
        displayNetworkErrorDialog()
    }
}
