package com.spascoding.configmasterdemo1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.json.JSONObject

@Composable
fun ConfigMasterDemo1Screen(
    viewModel: DemoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val receivedConfigs by viewModel.receivedConfigs.collectAsState()
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
            }
        }) {
            Text("Fetch Configuration for App")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(receivedConfigs) { config ->
                ConfigCard(config, viewModel)
            }
        }
    }
}


@Composable
fun ConfigCard(config: ConfigItem, viewModel: DemoViewModel) {
    var jsonText by remember { mutableStateOf(config.jsonData) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("App ID: ${config.appId}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = jsonText,
                onValueChange = { jsonText = it },
                label = { Text("JSON Data") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.updateConfig(config.appId, jsonText) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Update")
                }
                Button(
                    onClick = { viewModel.deleteConfig(config.appId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}
