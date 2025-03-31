package com.spascoding.configmasterdemo1

import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ConfigMasterDemo1Screen(
    viewModel: DemoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val receivedConfigs by viewModel.receivedConfigs.collectAsState()

    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Config Master Demo 1")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.addConfig() }) { Text("Add Config") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.updateConfig() }) { Text("Update Config") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.deleteConfig() }) { Text("Delete Config") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.fetchConfig() }) { Text("Receive and Show Config") }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(receivedConfigs) { config ->
                ConfigRow(config)
            }
        }
    }
}

@Composable
fun ConfigRow(config: ConfigItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = config.key, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(text = config.value, color = Color.Blue, modifier = Modifier.weight(1f))
    }
}