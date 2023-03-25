package com.dnd.smartroute.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val tabSelectedIndex = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = tabSelectedIndex.value,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = tabSelectedIndex.value == 0,
                    onClick = { tabSelectedIndex.value = 0 }
                ) {
                    Text(
                        text = "Вход",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Tab(
                    selected = tabSelectedIndex.value == 1,
                    onClick = { tabSelectedIndex.value = 1 }
                ) {
                    Text(
                        text = "Регистрация",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        if (tabSelectedIndex.value == 0) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Вход")
            }
        } else {
            // TODO: Add registration form
        }
    }
}