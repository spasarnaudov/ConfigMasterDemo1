package com.spascoding.configmasterdemo1

import android.app.Application
import android.content.ContentValues
import android.net.Uri
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

    private val AUTHORITY = "com.spascoding.configmaster.data.provider.ConfigProvider"
    private val CONTENT_URI = Uri.parse("content://$AUTHORITY/config")

    fun addConfig(configName: String, jsonData: String) {
        val values = ContentValues().apply {
            put("configName", configName)
            put("jsonData", jsonData)
        }
        application.contentResolver.insert(CONTENT_URI, values)
        fetchConfig(configName)
    }

    fun fetchConfig(configName: String) {
        viewModelScope.launch {
            val cursor = application.contentResolver.query(
                CONTENT_URI,
                null,
                null,
                arrayOf(configName),
                null
            )

            var config: ConfigItem? = null

            cursor?.use {
                if (it.moveToFirst()) {
                    val configName = it.getColumnIndex("configName")
                    val jsonIndex = it.getColumnIndex("jsonData")

                    if (configName != -1 && jsonIndex != -1) {
                        val id = it.getString(configName)
                        val json = it.getString(jsonIndex)
                        config = ConfigItem(id, json ?: "")
                    }
                }
            }

            _receivedConfig.value = config
        }
    }

}
