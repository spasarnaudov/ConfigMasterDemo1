package com.spascoding.configmasterhelper.kotlinhelper

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log

object ConfigMasterHelper {

    private val CONFIG_URI: Uri =
        Uri.parse("content://com.spascoding.configmaster.data.provider.ConfigProvider/config")

    /**
     * Insert or update configuration into ConfigMaster
     */
    fun insertConfig(context: Context, configName: String, jsonData: String) {
        val values = ContentValues().apply {
            put("configName", configName)
            put("jsonData", jsonData)
        }
        context.contentResolver.insert(CONFIG_URI, values)
    }

    /**
     * Fetch configuration from ConfigMaster
     */
    fun fetchConfig(context: Context, configName: String): String? {
        val cursor = context.contentResolver.query(
            CONFIG_URI,
            null,
            null,
            arrayOf(configName),
            null
        )

        var jsonData: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                jsonData = it.getString(it.getColumnIndexOrThrow("jsonData"))
                Log.d("ConfigMasterHelper", "Fetched config = $jsonData")
            }
        }
        return jsonData
    }
}
