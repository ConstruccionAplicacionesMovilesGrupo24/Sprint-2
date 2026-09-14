package com.campusmeal.android.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.campusmeal.android.core.designsystem.CampusMealTheme
import com.campusmeal.android.navigation.CampusMealNavHost

@Composable
fun CampusMealApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        CampusMealNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CampusMealAppPreview() {
    CampusMealTheme {
        CampusMealApp()
    }
}
