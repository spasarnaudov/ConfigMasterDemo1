package com.spascoding.configmasterdemo1.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spascoding.configmasterdemo1.ConfigItem

@Composable
fun AddConfigDialog(
    onDismiss: () -> Unit,
    onAdd: (String, List<ConfigItem>) -> Unit,
    initialAppId: String = "",
    initialKeyValuePairs: List<ConfigItem> = emptyList()
) {
    var appId by remember { mutableStateOf(initialAppId) }
    val keyValueList = remember { mutableStateListOf<ConfigItem>().apply { addAll(initialKeyValuePairs.ifEmpty { listOf(ConfigItem()) }) } }

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
                                value = pair.name,
                                onValueChange = {
                                    keyValueList[index] = pair.copy(name = it)
                                },
                                label = { Text("Key") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                            )
                            OutlinedTextField(
                                value = pair.jsonData,
                                onValueChange = {
                                    keyValueList[index] = pair.copy(jsonData = it)
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
                    keyValueList.add(ConfigItem())
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