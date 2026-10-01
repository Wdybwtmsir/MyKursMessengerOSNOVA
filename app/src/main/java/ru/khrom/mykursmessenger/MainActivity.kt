package ru.khrom.mykursmessenger

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.khrom.mykursmessenger.presentation.*
import ru.khrom.mykursmessenger.ui.theme.MyKursMessengerTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
        )

        setContent {
            MyKursMessengerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startScreen = "splash"

                    NavHost(
                        navController = navController,
                        startDestination = startScreen,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = "splash") {
                            SplashScreen(onNavigateNext = { navController.navigate("welcome") })
                        }

                        composable(route = "welcome") {
                            WelcomeScreen(onGetStartedClick = { navController.navigate("login") })
                        }

                        composable(route = "login") {
                            LoginScreen(
                                onAuthSuccess = { role ->
                                    if (role == "doctor") {
                                        navController.navigate("doctor_dashboard")
                                    } else {
                                        navController.navigate("home_screen")
                                    }
                                }
                            )
                        }

                        composable(route = "register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("home_screen")
                                }
                            )
                        }

                        composable(route = "home_screen") {
                            HomeScreen(
                                onNotificationClick = {  },
                                onSettingsClick = {  },
                                onDoctorsTabClick = { navController.navigate("doctor_list") }
                            )
                        }

                        composable(route = "doctor_list") {
                            DoctorListScreen(
                                onDoctorClick = { doctorId ->
                                    navController.navigate("chat_screen/$doctorId")
                                }
                            )
                        }

                        composable(route = "profile") {
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onEditProfileClick = {  },
                                onLogoutClick = {
                                    navController.navigate("welcome") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(route = "appointments_screen") {
                            AppointmentsScreen()
                        }

                        composable(route = "doctor_dashboard") {
                            DoctorDashboardScreen(
                                onPatientChatClick = { patientId ->
                                    navController.navigate("chat_screen/$patientId")
                                },
                                onProfileClick = {  },
                                onLogoutClick = {
                                    navController.navigate("welcome") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(route = "call_screen/{callId}") { backStackEntry ->
                            val callId = backStackEntry.arguments?.getString("callId") ?: ""
                            CallScreen(
                                callId = callId,
                                onDisconnectClick = { navController.popBackStack() }
                            )
                        }

                        composable(route = "video_call_screen/{callId}") { backStackEntry ->
                            val callId = backStackEntry.arguments?.getString("callId") ?: ""
                            VideoCallScreen(
                                callId = callId,
                                onDisconnectClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
