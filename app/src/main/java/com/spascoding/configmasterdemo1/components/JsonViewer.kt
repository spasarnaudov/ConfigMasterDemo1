package com.spascoding.configmasterdemo1.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spascoding.configmasterdemo1.ConfigItem
import org.json.JSONObject

@Composable
fun JsonViewer(
    config: ConfigItem,
    onEditConfirmed: (editedConfigItems: List<ConfigItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    val formattedJson = remember(config.jsonData) {
        try {
            JSONObject(config.jsonData).toString(4)
        } catch (e: Exception) {
            config.jsonData
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { showConfirmDialog = true }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(8.dp)
        ) {
            Text(
                text = formattedJson,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                style = LocalTextStyle.current.copy(
                    lineHeight = 20.sp
                )
            )
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Edit Configuration?") },
            text = { Text("Do you want to edit the configuration for App ID: ${config.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onEditConfirmed(
                        parseJsonToKeyValueList(config.jsonData)
                    )
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun parseJsonToKeyValueList(json: String): List<ConfigItem> {
    val list = mutableListOf<ConfigItem>()
    val jsonObject = JSONObject(json)
    for (key in jsonObject.keys()) {
        list.add(ConfigItem(key, jsonObject.optString(key, "")))
    }
    return list
}