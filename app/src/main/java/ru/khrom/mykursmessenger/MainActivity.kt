package ru.khrom.mykursmessenger

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.khrom.mykursmessenger.presentation.*
import ru.khrom.mykursmessenger.ui.theme.MedSurface
import ru.khrom.mykursmessenger.ui.theme.MyKursMessengerTheme
import ru.khrom.mykursmessenger.ui.theme.SplashBackground

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
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Показываем BottomBar только для пациента на его экранах
                    val showBottomBar = currentRoute in listOf("home_screen", "doctor_list", "profile", "appointments_screen")

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar(containerColor = SplashBackground, tonalElevation = 8.dp) {
                                    NavigationBarItem(
                                        selected = currentRoute == "home_screen",
                                        onClick = { if (currentRoute != "home_screen") navController.navigate("home_screen") { popUpTo("home_screen") { inclusive = true } } },
                                        icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(26.dp)) },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SplashBackground, unselectedIconColor = MedSurface, indicatorColor = MedSurface)
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "doctor_list",
                                        onClick = { if (currentRoute != "doctor_list") navController.navigate("doctor_list") { popUpTo("home_screen") { inclusive = false } } },
                                        icon = { Icon(Icons.Default.Chat, null, modifier = Modifier.size(24.dp)) },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SplashBackground, unselectedIconColor = MedSurface, indicatorColor = MedSurface)
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "profile",
                                        onClick = { if (currentRoute != "profile") navController.navigate("profile") { popUpTo("home_screen") { inclusive = false } } },
                                        icon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(26.dp)) },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SplashBackground, unselectedIconColor = MedSurface, indicatorColor = MedSurface)
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "appointments_screen",
                                        onClick = { if (currentRoute != "appointments_screen") navController.navigate("appointments_screen") { popUpTo("home_screen") { inclusive = false } } },
                                        icon = { Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(24.dp)) },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = SplashBackground, unselectedIconColor = MedSurface, indicatorColor = MedSurface)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(navController = navController, startDestination = "splash", modifier = Modifier.padding(innerPadding)) {
                            composable("splash") {
                                SplashScreen(onNavigateNext = { route -> navController.navigate(route) { popUpTo("splash") { inclusive = true } } })
                            }
                            composable("welcome") { WelcomeScreen(onGetStartedClick = { navController.navigate("login") }) }

                            composable("login") {
                                LoginScreen(
                                    onAuthSuccess = { role ->
                                        if (role == "doctor") {
                                            navController.navigate("doctor_dashboard") { popUpTo("welcome") { inclusive = true } }
                                        } else {
                                            navController.navigate("home_screen") { popUpTo("welcome") { inclusive = true } }
                                        }
                                    },
                                    onSignUpClick = { navController.navigate("signup") }
                                )
                            }

                            composable("signup") {
                                SignUpScreen(
                                    onNextClick = { email -> navController.navigate("set_password/$email") },
                                    onBackToLoginClick = { navController.navigate("login") { popUpTo("login") { inclusive = true } } }
                                )
                            }

                            composable(
                                route = "set_password/{email}",
                                arguments = listOf(navArgument("email") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val email = backStackEntry.arguments?.getString("email") ?: ""
                                SetPasswordScreen(email = email, onRegisterSuccess = { navController.navigate("login") { popUpTo("welcome") { inclusive = true } } })
                            }

                            composable("home_screen") {
                                HomeScreen(
                                    onDoctorClick = { doctorId -> navController.navigate("doctor_details/$doctorId") },
                                    onNotificationClick = { navController.navigate("notification_screen") },
                                    onSettingsClick = { navController.navigate("settings_screen") },
                                    onDoctorsTabClick = { navController.navigate("doctor_list") }
                                )
                            }

                            // ЭКРАН ВРАЧА
                            composable("doctor_dashboard") {
                                DoctorDashboardScreen(
                                    onPatientChatClick = { patientId -> navController.navigate("chat_screen/$patientId") },
                                    onLogoutClick = { navController.navigate("welcome") { popUpTo(0) { inclusive = true } } }
                                )
                            }

                            composable("doctor_list") {
                                DoctorListScreen(
                                    onDoctorClick = { doctorId -> navController.navigate("doctor_details/$doctorId") },
                                    onProfileClick = { navController.navigate("profile") }
                                )
                            }

                            composable(
                                route = "doctor_details/{doctorId}",
                                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                DoctorDetailsScreen(doctorId = doctorId, onBackClick = { navController.popBackStack() }, onStartChatClick = { navController.navigate("schedule_screen/$doctorId") })
                            }

                            composable(
                                route = "schedule_screen/{doctorId}",
                                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                ScheduleScreen(doctorId = doctorId, onBackClick = { navController.popBackStack() }, onBookClick = { date, slot, forWhom, problem -> navController.navigate("appointment_details_screen/$doctorId/$date/$slot/$forWhom/$problem") })
                            }
                            composable(
                                route = "appointment_details_screen/{doctorId}/{date}/{slot}/{forWhom}/{problem}",
                                arguments = listOf(navArgument("doctorId") { type = NavType.StringType }, navArgument("date") { type = NavType.StringType }, navArgument("slot") { type = NavType.StringType }, navArgument("forWhom") { type = NavType.StringType }, navArgument("problem") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                val date = backStackEntry.arguments?.getString("date") ?: ""
                                val slot = backStackEntry.arguments?.getString("slot") ?: ""
                                val forWhom = backStackEntry.arguments?.getString("forWhom") ?: ""
                                val problem = backStackEntry.arguments?.getString("problem") ?: ""
                                AppointmentDetailsScreen(doctorId = doctorId, dateText = date, timeSlot = slot, bookingFor = forWhom, problemDescription = problem, onBackClick = { navController.popBackStack() }, onConfirmClick = { navController.navigate("review_summary_screen/$doctorId/$date/$slot/$forWhom/$problem") })
                            }
                            composable(
                                route = "review_summary_screen/{doctorId}/{date}/{slot}/{forWhom}/{problem}",
                                arguments = listOf(navArgument("doctorId") { type = NavType.StringType }, navArgument("date") { type = NavType.StringType }, navArgument("slot") { type = NavType.StringType }, navArgument("forWhom") { type = NavType.StringType }, navArgument("problem") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                val date = backStackEntry.arguments?.getString("date") ?: ""
                                val slot = backStackEntry.arguments?.getString("slot") ?: ""
                                val forWhom = backStackEntry.arguments?.getString("forWhom") ?: ""
                                val problem = backStackEntry.arguments?.getString("problem") ?: ""
                                ReviewSummaryScreen(doctorId = doctorId, dateText = date, timeSlot = slot, bookingFor = forWhom, onBackClick = { navController.popBackStack() }, onPayNowClick = { navController.navigate("payment_screen/$doctorId/$date/$slot/$forWhom/$problem") })
                            }
                            composable(
                                route = "payment_screen/{doctorId}/{date}/{slot}/{forWhom}/{problem}",
                                arguments = listOf(navArgument("doctorId") { type = NavType.StringType }, navArgument("date") { type = NavType.StringType }, navArgument("slot") { type = NavType.StringType }, navArgument("forWhom") { type = NavType.StringType }, navArgument("problem") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                val date = backStackEntry.arguments?.getString("date") ?: ""
                                val slot = backStackEntry.arguments?.getString("slot") ?: ""
                                val forWhom = backStackEntry.arguments?.getString("forWhom") ?: ""
                                val problem = backStackEntry.arguments?.getString("problem") ?: ""
                                PaymentScreen(doctorId = doctorId, dateText = date, timeSlot = slot, bookingFor = forWhom, problemDescription = problem, onBackClick = { navController.popBackStack() }, onPaymentComplete = { navController.navigate("chat_screen/$doctorId") { popUpTo("home_screen") { inclusive = false } } })
                            }
                            composable(
                                route = "chat_screen/{doctorId}",
                                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                                ChatScreen(doctorId = doctorId, onBackClick = { navController.popBackStack() }, onCallClick = { navController.navigate("call_screen/$doctorId") }, onVideoCallClick = { navController.navigate("video_call_screen/$doctorId") })
                            }
                            composable("profile") {
                                ProfileScreen(onBackClick = { navController.popBackStack() }, onEditProfileClick = { navController.navigate("edit_profile") }, onLogoutSuccess = { navController.navigate("welcome") { popUpTo(0) { inclusive = true } } })
                            }
                            composable("edit_profile") {
                                EditProfileScreen(onBackClick = { navController.popBackStack() }, onSaveClick = { navController.popBackStack() })
                            }
                            composable("appointments_screen") {
                                AppointmentsScreen(onReviewClick = { doctorId -> navController.navigate("review_screen/$doctorId") })
                            }
                            composable("notification_screen") { NotificationScreen(onBackClick = { navController.popBackStack() }) }
                            composable("notification_setting_screen") { NotificationSettingsScreen(onBackClick = { navController.popBackStack() }) }
                            composable("settings_screen") {
                                SettingsScreen(onBackClick = { navController.popBackStack() }, onNotificationSettingClick = { navController.navigate("notification_setting_screen") }, onPasswordManagerClick = { navController.navigate("password_manager_screen") }, onPrivacyPolicyClick = { navController.navigate("privacy_policy_screen") }, onHelpCenterClick = { navController.navigate("help_center_screen") }, onAccountDeletedSuccess = { navController.navigate("welcome") { popUpTo(0) { inclusive = true } } })
                            }
                            composable("password_manager_screen") { PasswordManagerScreen(onBackClick = { navController.popBackStack() }, onChangePasswordSuccess = { navController.popBackStack() }) }
                            composable("help_center_screen") { HelpCenterScreen(onBackClick = { navController.popBackStack() }) }
                            composable("privacy_policy_screen") { PrivacyPolicyScreen(onBackClick = { navController.popBackStack() }) }
                        }
                    }
                }
            }
        }
    }
}