package com.spascoding.configmasterdemo1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spascoding.configmasterdemo1.components.AddConfigDialog
import com.spascoding.configmasterdemo1.components.JsonViewer
import org.json.JSONObject

@Composable
fun ConfigMasterDemo1Screen(
    viewModel: DemoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val receivedConfig by viewModel.receivedConfig.collectAsState()
    var fetchConfig by remember { mutableStateOf("") }
    var fetchConfigParameter by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var editAppId by remember { mutableStateOf("") }
    var configItems by remember { mutableStateOf<List<ConfigItem>>(emptyList()) }

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

        ConfigSearchBar(
            label = "Config name",
            value = fetchConfig,
            onValueChange = {
                fetchConfig = it
                if (fetchConfig.isNotBlank()) {
                    viewModel.fetchConfigParam(fetchConfig.trim())
                }
            },
            onClear = {
                fetchConfig = ""
                viewModel.fetchConfigParam()
            }
        )

        ConfigSearchBar(
            label = "Parameter name",
            value = fetchConfigParameter,
            onValueChange = {
                fetchConfigParameter = it
                if (fetchConfig.isNotBlank()) {
                    viewModel.fetchConfigParam(fetchConfig.trim(), fetchConfigParameter.trim())
                }
            },
            onClear = {
                fetchConfigParameter = ""
                viewModel.fetchConfigParam()
            }
        )

        Spacer(Modifier.height(16.dp))

        if (fetchConfig.isNotBlank()) {
            receivedConfig?.let { config ->
                JsonViewer(
                    config = config,
                    onEditConfirmed = {
                        configItems = it
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
                keyValuePairs.forEach { config ->
                    if (config.name.isNotBlank()) {
                        jsonObject.put(config.name, config.jsonData)
                    }
                }
                viewModel.addConfig(appId, jsonObject.toString())
                showAddDialog = false
            },
            initialAppId = if (isEditMode) editAppId else "",
            initialKeyValuePairs = if (isEditMode) configItems else emptyList()
        )
    }

}

@Composable
fun ConfigSearchBar(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(onClick = {
                    onClear()
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        }
    )
}
