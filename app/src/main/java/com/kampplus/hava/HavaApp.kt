package com.kampplus.hava

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kampplus.hava.core.navigation.HavaNavHost

@Composable
fun HavaApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(modifier = modifier) { innerPadding ->
        HavaNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}
