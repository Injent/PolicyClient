package ui.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import currentPlayer
import ipGlobal
import koin
import models.Player

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JoinScreen(
    viewModel: JoinViewModel = koin.get(),
    onStart: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("Player") }
    var ip by remember { mutableStateOf("26.20.11.247:25565") }

    Column {
        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            singleLine = true
        )
        TextField(
            value = ip,
            onValueChange = {
                ip = it
            },
            singleLine = true
        )

        Row(
            Modifier.border(2.dp, Color.White)
        ) {
            Text("Цвет", fontSize = 24.sp)
            Box(Modifier
                .size(32.dp)
                .background(uiState.color)
            )
        }
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.width((40 * 4).dp),
        ) {
            items(uiState.colors) { color ->
                Box(
                    Modifier
                        .padding(4.dp)
                        .onClick {
                            viewModel.selectColor(color)
                        }
                ) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .background(color)
                    )
                }
            }
        }

        Button(
            onClick = {
                ipGlobal = ip
                currentPlayer = Player(name = name, rgb = uiState.color.toArgb())
                onStart()
            }
        ) {
            Text("Алег")
        }
    }

}