package com.assasement.dummyShop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navController: NavHostController,
    startingScreen: AppRoute,
    modifier: Modifier,
) {
    val appNavController = remember(navController) {
        AppComposeNavController(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startingScreen,
        modifier = modifier,
    ) {

    }
}
