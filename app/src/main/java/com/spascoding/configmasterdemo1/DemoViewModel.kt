package com.spascoding.configmasterdemo1

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(private val application: Application) : ViewModel() {

    val appId = "DemoApp1"
    val AUTHORITY = "com.spascoding.configmaster.data.provider.ConfigProvider"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/config")

    private val _receivedConfigs = MutableStateFlow<List<ConfigItem>>(emptyList())
    val receivedConfigs: StateFlow<List<ConfigItem>> = _receivedConfigs

    fun addConfig() {
        val values = ContentValues().apply {
            put("appId", appId)
            put("jsonData", """{"key1": "value1", "key2": "value2"}""")
        }
        application.applicationContext.contentResolver.insert(CONTENT_URI, values)
    }

    fun updateConfig() {
        val values = ContentValues().apply {
            put("appId", appId)
            put("jsonData", """{"key1": "updatedValue1", "key2": "updatedValue2"}""")
        }
        application.applicationContext.contentResolver.update(CONTENT_URI, values, null, null)
    }

    fun deleteConfig() {
        application.applicationContext.contentResolver.delete(CONTENT_URI, null, arrayOf(appId))
    }

    fun fetchConfig() {
        viewModelScope.launch {
            val cursor = application.applicationContext.contentResolver.query(CONTENT_URI, null, null, arrayOf(appId), null)
            val configs = mutableListOf<ConfigItem>()

            cursor?.use {
                while (it.moveToNext()) {
                    val key = it.getString(it.getColumnIndexOrThrow("appId"))
                    val value = it.getString(it.getColumnIndexOrThrow("jsonData"))
                    configs.add(ConfigItem(key, value))
                }
            }

            _receivedConfigs.value = configs
        }
    }

}