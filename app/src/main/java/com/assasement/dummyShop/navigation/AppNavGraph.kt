package com.assasement.dummyShop.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.assasement.dummyShop.R
import com.assasement.dummyShop.view.home.HomeScreen
import com.assasement.dummyShop.view.product.ProductScreen
import com.assasement.dummyShop.view.search.SearchScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Search : Screen("search", "Search", Icons.Filled.Search)
    data object Cart : Screen("cart", "Cart", Icons.Filled.ShoppingCart)
    data object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    data object Product : Screen("product/{productId}", "Product", Icons.Filled.Home) {
        fun createRoute(productId: Int) = "product/$productId"
    }
}

private val bottomScreens = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Cart,
    Screen.Profile,
)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = bottomScreens.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (selected) return@NavigationBarItem

                                if (screen == Screen.Home) {
                                    val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                                    if (!popped) {
                                        navController.navigate(Screen.Home.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                } else {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSearchClick = { navController.navigate(Screen.Search.route) { launchSingleTop = true } },
                    onProductClick = { productId -> navController.navigate(Screen.Product.createRoute(productId)) },
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(onProductClick = { productId -> navController.navigate(Screen.Product.createRoute(productId)) })
            }
            composable(Screen.Cart.route) {
                StubScreen()
            }
            composable(Screen.Profile.route) {
                StubScreen()
            }
            composable(
                route = Screen.Product.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType }),
            ) { entry ->
                val productId = entry.arguments?.getInt("productId") ?: return@composable
                ProductScreen(productId = productId, onBackClick = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun StubScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.under_construction),
                contentDescription = null,
                modifier = Modifier.size(250.dp)

            )
        }

}



