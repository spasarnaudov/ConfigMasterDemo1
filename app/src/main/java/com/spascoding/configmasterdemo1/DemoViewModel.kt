package com.spascoding.configmasterdemo1

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _receivedConfig = MutableStateFlow<ConfigItem?>(null)
    val receivedConfig: StateFlow<ConfigItem?> = _receivedConfig

    fun addConfig(configName: String, jsonData: String) {
        com.spascoding.configmasterhelper.ConfigMasterHelper.insertConfig(application, configName, jsonData)
//        com.spascoding.configmasterhelper.javahelper.ConfigMasterHelper.insertConfig(application, configName, jsonData)
    }

    fun fetchConfig(configName: String) {

        viewModelScope.launch {
            val json = com.spascoding.configmasterhelper.ConfigMasterHelper.fetchConfig(application, configName)
//            val json = com.spascoding.configmasterhelper.javahelper.ConfigMasterHelper.fetchConfig(application, configName)
            _receivedConfig.value = ConfigItem(configName, json ?: "")
        }
    }

}
