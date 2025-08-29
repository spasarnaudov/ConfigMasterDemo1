package com.spascoding.configmasterdemo1

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.json.JSONObject

@Composable
fun ConfigMasterDemo1Screen(
    viewModel: DemoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val receivedConfig by viewModel.receivedConfig.collectAsState()
    var fetchConfig by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var editAppId by remember { mutableStateOf("") }
    var editPairs by remember { mutableStateOf<List<KeyValuePair>>(emptyList()) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ConfigMaster Demo", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(24.dp))
            Button(
                onClick = { showAddDialog = true}
            ) {
                Icon(Icons.Filled.Add, "Add Configuration")
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = fetchConfig,
            onValueChange = {
                fetchConfig = it
                if (fetchConfig.isNotBlank()) {
                    viewModel.fetchConfig(fetchConfig.trim())
                }
            },
            label = { Text("Config to Fetch") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            trailingIcon = {
                IconButton(onClick = {
                    fetchConfig = ""
                    viewModel.fetchConfig(fetchConfig.trim())
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        if (fetchConfig.isNotBlank()) {
            receivedConfig?.let { config ->
                JsonViewer(
                    config = config,
                    onEditConfirmed = { appId, pairs ->
                        editAppId = appId
                        editPairs = pairs
                        isEditMode = true
                        showAddDialog = true
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddConfigDialog(
            onDismiss = {
                showAddDialog = false
                isEditMode = false
            },
            onAdd = { appId, keyValuePairs ->
                val jsonObject = JSONObject()
                keyValuePairs.forEach { pair ->
                    if (pair.key.isNotBlank()) {
                        jsonObject.put(pair.key, pair.value)
                    }
                }
                viewModel.addConfig(appId, jsonObject.toString())
                showAddDialog = false
            },
            initialAppId = if (isEditMode) editAppId else "",
            initialKeyValuePairs = if (isEditMode) editPairs else emptyList()
        )
    }

}

@Composable
fun JsonViewer(
    config: ConfigItem,
    onEditConfirmed: (appId: String, keyValuePairs: List<KeyValuePair>) -> Unit,
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

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { showConfirmDialog = true }
    ) {
        Text(
            text = "App ID: ${config.name}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(8.dp)
        ) {
            Text(
                text = formattedJson,
                modifier = Modifier
                    .verticalScroll(scrollState)
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
                        config.name,
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

@Composable
fun AddConfigDialog(
    onDismiss: () -> Unit,
    onAdd: (String, List<KeyValuePair>) -> Unit,
    initialAppId: String = "",
    initialKeyValuePairs: List<KeyValuePair> = emptyList()
) {
    var appId by remember { mutableStateOf(initialAppId) }
    val keyValueList = remember { mutableStateListOf<KeyValuePair>().apply { addAll(initialKeyValuePairs.ifEmpty { listOf(KeyValuePair()) }) } }

    val listState = rememberLazyListState()
    var shouldScroll by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialAppId.isBlank()) "New Configuration" else "Edit Configuration") },
        text = {
            Column {
                OutlinedTextField(
                    value = appId,
                    onValueChange = { appId = it },
                    label = { Text("App ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = initialAppId.isBlank() // prevent editing appId in edit mode
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

                // Auto scroll to last item
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
                    onAdd(appId.trim(), keyValueList.toList())
                }
            }) {
                Text(if (initialAppId.isBlank()) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun parseJsonToKeyValueList(json: String): List<KeyValuePair> {
    val list = mutableListOf<KeyValuePair>()
    val jsonObject = JSONObject(json)
    for (key in jsonObject.keys()) {
        list.add(KeyValuePair(key, jsonObject.optString(key, "")))
    }
    return list
}