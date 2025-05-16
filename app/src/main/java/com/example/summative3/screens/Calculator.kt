package com.example.summative3.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Calculator(navController: NavController) {
    // Remember the logic instance so it survives recompositions
    val calculatorLogic = remember { CalculatorLogic() }

    // Hold the display state which reflects calculatorLogic.display
    var display by remember { mutableStateOf(calculatorLogic.display) }

    // Keep display updated when logic changes
    LaunchedEffect(calculatorLogic.display) {
        display = calculatorLogic.display
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calculator",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = display,
                fontSize = 40.sp,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        val buttonRows = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttonRows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        Button(
                            onClick = {
                                when (btn) {
                                    in "0".."9" -> calculatorLogic.onDigit(btn)
                                    "+", "-", "×", "÷" -> calculatorLogic.onOperator(btn)
                                    "=" -> calculatorLogic.onEquals()
                                    "C" -> calculatorLogic.onClear()
                                }
                                // Update display after every click
                                display = calculatorLogic.display
                            },
                            shape = RectangleShape,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text(btn, fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}
