package com.kraft.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kraft.calculator.presentation.CalculatorScreen
import com.kraft.calculator.ui.theme.KraftTheme

class MainActivity : ComponentActivity() {

    private val lock = Any()
    private var orientationSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keep portrait; no rotation
        enableEdgeToEdge()
        setContent {
            KraftTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: com.kraft.calculator.presentation.CalculatorViewModel = viewModel()
                    CalculatorScreen(viewModel = viewModel)
                }
            }
        }
    }
}
