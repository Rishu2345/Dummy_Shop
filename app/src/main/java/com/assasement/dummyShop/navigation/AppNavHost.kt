package com.assasement.dummyShop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import clickretina.assasement.skillforge.view.courseDetails.CourseDetailsScreen
import clickretina.assasement.skillforge.view.home.HomeScreen
import clickretina.assasement.skillforge.view.lessonPlayer.LessonPlayerScreen
import clickretina.assasement.skillforge.viewModel.courseDetailsViewModel.CourseDetailsViewModel
import clickretina.assasement.skillforge.viewModel.homeScreenViewModel.HomeViewModel
import clickretina.assasement.skillforge.viewModel.lessonPlayerViewModel.LessonPlayerViewModel

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
        composable<HomeScreenRoute> {
            val viewModel: HomeViewModel = appViewModel(navController = appNavController)
            HomeScreen(viewModel = viewModel)
        }
        composable<CourseDetailsRoute> {
            val viewModel: CourseDetailsViewModel = appViewModel(navController = appNavController)
            CourseDetailsScreen(viewModel = viewModel)
        }
        composable<LessonPlayerRoute> {
            val viewModel: LessonPlayerViewModel = appViewModel(navController = appNavController)
            LessonPlayerScreen(viewModel = viewModel)
        }
    }
}
