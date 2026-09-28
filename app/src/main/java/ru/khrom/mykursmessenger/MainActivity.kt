package ru.khrom.mykursmessenger

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.khrom.mykursmessenger.presentation.DoctorListScreen
import ru.khrom.mykursmessenger.presentation.LoginScreen
import ru.khrom.mykursmessenger.presentation.SetPasswordScreen
import ru.khrom.mykursmessenger.presentation.SignUpScreen
import ru.khrom.mykursmessenger.presentation.SplashScreen
import ru.khrom.mykursmessenger.presentation.WelcomeScreen
import ru.khrom.mykursmessenger.ui.theme.MyKursMessengerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyKursMessengerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "splash") {

                        // 1. Сплеш-экран
                        composable("splash") {
                            SplashScreen(onNavigateNext = { route ->
                                navController.navigate(route) {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        // 2. Экран приветствия
                        composable("welcome") {
                            WelcomeScreen(onGetStartedClick = {
                                navController.navigate("login")
                            })
                        }

                        // 3. Экран авторизации (Вход)
                        composable("login") {
                            LoginScreen(
                                onAuthSuccess = {
                                    Toast.makeText(this@MainActivity, "Вход успешен!", Toast.LENGTH_SHORT).show()
                                    // После успешного входа перенаправляем на список врачей
                                    navController.navigate("doctor_list") {
                                        popUpTo("welcome") { inclusive = true } // Очищаем экраны входа из истории
                                    }
                                },
                                onSignUpClick = {
                                    navController.navigate("signup")
                                }
                            )
                        }

                        // 4. Регистрация (Шаг 1: Имя и Почта)
                        composable("signup") {
                            SignUpScreen(
                                onNextClick = { email ->
                                    navController.navigate("set_password/$email")
                                },
                                onBackToLoginClick = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 5. Установка пароля (Шаг 2)
                        composable(
                            route = "set_password/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            SetPasswordScreen(
                                email = email,
                                onRegisterSuccess = {
                                    Toast.makeText(this@MainActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 6. Главный экран: Список врачей
                        composable("doctor_list") {
                            DoctorListScreen(onDoctorClick = { doctorId ->
                                // Сюда повесим открытие конкретного чата на следующем шаге
                            })
                        }
                    }
                }
            }
        }
    }
}
