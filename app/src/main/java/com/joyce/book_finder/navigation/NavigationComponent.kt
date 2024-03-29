package com.joyce.book_finder.navigation

import LoginScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joyce.book_finder.ui.login.LoginViewModel
import com.joyce.book_finder.ui.splash.SplashScreen

sealed class Screen(val route: String){
    object Splash : Screen("splash_route")
    object Login : Screen("login_route")
    object Books : Screen("books_route")
}

@Composable
fun NavigationComponent(
    navController: NavHostController,
    loginVM: LoginViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route){
            LoginScreen(loginVM)
        }
//        composable(Screen.Books.route) {
//            BooksScreen(booksVM)
//        }
    }
}