package com.spascoding.configmasterdemo1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.json.JSONObject

@Composable
fun ConfigMasterDemo1Screen(
    viewModel: DemoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val receivedConfig by viewModel.receivedConfig.collectAsState()
    var newAppId by remember { mutableStateOf("") }
    val keyValueList = remember { mutableStateListOf(KeyValuePair()) }
    var newJson by remember { mutableStateOf("") }
    var fetchAppId by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Config Master Demo 1", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = newAppId,
            onValueChange = { newAppId = it },
            label = { Text("App ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Text("Key-Value Pairs", fontWeight = FontWeight.Bold)

        keyValueList.forEachIndexed { index, pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = pair.key,
                    onValueChange = { keyValueList[index] = pair.copy(key = it) },
                    label = { Text("Key") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = pair.value,
                    onValueChange = { keyValueList[index] = pair.copy(value = it) },
                    label = { Text("Value") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                IconButton(onClick = { if (keyValueList.size > 1) keyValueList.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Pair")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            keyValueList.add(KeyValuePair())
        }) {
            Text("Add Key-Value Pair")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (newAppId.isNotBlank()) {
                val jsonObject = JSONObject()
                keyValueList.forEach { pair ->
                    if (pair.key.isNotBlank()) {
                        jsonObject.put(pair.key, pair.value)
                    }
                }
                viewModel.addConfig(newAppId.trim(), jsonObject.toString())
                newAppId = ""
                newJson = ""
                keyValueList.clear()
                keyValueList.add(KeyValuePair())
                keyboardController?.hide()
            }
        }) {
            Text("Add Configuration")
        }

        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = fetchAppId,
            onValueChange = { fetchAppId = it },
            label = { Text("App ID to Fetch") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (fetchAppId.isNotBlank()) {
                viewModel.fetchConfig(fetchAppId.trim())
                keyboardController?.hide()
            }
        }) {
            Text("Fetch Configuration")
        }

        Spacer(Modifier.height(16.dp))

        receivedConfig?.let { config ->
            OutlinedTextField(
                value = config.jsonData,
                onValueChange = { },
                label = { Text("App ID: ${config.appId}") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
