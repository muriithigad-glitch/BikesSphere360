package com.deepseek.bikesphere360.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deepseek.bikesphere360.screens.*

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH) { SplashScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_HOME) { HomeScreen(navController) }
        composable(ROUTE_ADD_PRODUCT) { AddProductScreen(navController) }
        composable(ROUTE_SETTINGS) { SettingsScreen(navController) }
        composable(ROUTE_PROFILE) { ProfileScreen(navController) }
        composable(ROUTE_ABOUT_US) { AboutUsScreen() }
        composable(ROUTE_CONTACT_US) { ContactUsScreen() }
        composable(ROUTE_REPORT_PROBLEM) { ReportProblemScreen() }
        composable(ROUTE_ADMIN_DASHBOARD) { AdminDashboardScreen(navController) }
        
        composable(
            ROUTE_CHECKOUT,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("productName") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getString("productId") ?: ""
            val name = backStackEntry.arguments?.getString("productName") ?: ""
            val price = backStackEntry.arguments?.getString("price") ?: ""
            SparePartCheckoutScreen(navController, pid, name, price)
        }

        composable(
            ROUTE_BOOK_APPOINTMENT,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("productName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getString("productId") ?: ""
            val name = backStackEntry.arguments?.getString("productName") ?: ""
            BikeBookingScreen(navController, pid, name)
        }

        composable(
            "category_select/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            CategorySelectionScreen(navController, type)
        }

        composable(
            ROUTE_VIEW_PRODUCTS,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("subCategory") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val subCategory = backStackEntry.arguments?.getString("subCategory") ?: ""
            ProductListScreen(navController, type, subCategory)
        }
    }
}
