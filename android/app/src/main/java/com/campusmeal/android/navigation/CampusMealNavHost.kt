package com.campusmeal.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.campusmeal.android.R

/**
 * Registers every route in [CampusMealRoute]. Feature screens replace the placeholders
 * as they are implemented.
 */
@Composable
fun CampusMealNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: CampusMealRoute = CampusMealRoute.Home,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<CampusMealRoute.Home> { PlaceholderMessage(stringResource(R.string.foundation_ready)) }
        composable<CampusMealRoute.Auth> { RoutePlaceholder("Auth") }
        composable<CampusMealRoute.Inventory> { RoutePlaceholder("Inventory") }
        composable<CampusMealRoute.Context> { RoutePlaceholder("Context") }
        composable<CampusMealRoute.Restaurants> { RoutePlaceholder("Restaurants") }
        composable<CampusMealRoute.Decision> { RoutePlaceholder("Decision") }
        composable<CampusMealRoute.Profile> { RoutePlaceholder("Profile") }
    }
}

@Composable
private fun RoutePlaceholder(routeName: String) {
    PlaceholderMessage(stringResource(R.string.route_placeholder, routeName))
}

@Composable
private fun PlaceholderMessage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
    }
}
