package com.spascoding.configmasterdemo1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var fetchAppId by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Config Master Demo 1", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // Show dialog when user clicks "Add Configuration"
        Button(onClick = { showAddDialog = true }) {
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

    if (showAddDialog) {
        AddConfigDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { appId, keyValuePairs ->
                val jsonObject = JSONObject()
                keyValuePairs.forEach { pair ->
                    if (pair.key.isNotBlank()) {
                        jsonObject.put(pair.key, pair.value)
                    }
                }
                viewModel.addConfig(appId.trim(), jsonObject.toString())
                showAddDialog = false
                keyboardController?.hide()
            }
        )
    }
}

@Composable
fun AddConfigDialog(
    onDismiss: () -> Unit,
    onAdd: (String, List<KeyValuePair>) -> Unit
) {
    var appId by remember { mutableStateOf("") }
    val keyValueList = remember { mutableStateListOf(KeyValuePair()) }

    val listState = rememberLazyListState()
    var shouldScroll by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Configuration") },
        text = {
            Column {
                OutlinedTextField(
                    value = appId,
                    onValueChange = { appId = it },
                    label = { Text("App ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text("Key-Value Pairs", fontWeight = FontWeight.Bold)

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .fillMaxWidth()
                ) {
                    items(keyValueList.size) { index ->
                        val pair = keyValueList[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = pair.key,
                                onValueChange = {
                                    keyValueList[index] = pair.copy(key = it)
                                },
                                label = { Text("Key") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                            )
                            OutlinedTextField(
                                value = pair.value,
                                onValueChange = {
                                    keyValueList[index] = pair.copy(value = it)
                                },
                                label = { Text("Value") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                            )
                            IconButton(onClick = {
                                if (keyValueList.size > 1) keyValueList.removeAt(index)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Pair")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(onClick = {
                    keyValueList.add(KeyValuePair())
                    shouldScroll = true
                }) {
                    Text("Add Key-Value Pair")
                }

                // Scroll to bottom when a new item is added
                LaunchedEffect(shouldScroll, keyValueList.size) {
                    if (shouldScroll) {
                        listState.animateScrollToItem(keyValueList.lastIndex)
                        shouldScroll = false
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (appId.isNotBlank()) {
                    onAdd(appId, keyValueList.toList())
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

