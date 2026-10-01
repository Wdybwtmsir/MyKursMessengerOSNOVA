package ru.khrom.mykursmessenger.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun SetPasswordScreen(email: String, onRegisterSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isDoctorRole by remember { mutableStateOf(false) } // Переключатель роли
    var isPending by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Придумайте пароль", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Шаг 2 из 2: Завершение настройки", fontSize = 15.sp, color = TextSecondary, fontFamily = FontFamily.Default)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ваше имя", fontFamily = FontFamily.Default) },
                textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, color = TextPrimary),
                enabled = !isPending,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль", fontFamily = FontFamily.Default) },
                textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, color = TextPrimary),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isPending,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface
                )
            )

            // Выбор роли пользователя
            Text("Выберите тип аккаунта:", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isDoctorRole = false },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (!isDoctorRole) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Пациент", color = if (!isDoctorRole) MedSurface else SplashBackground)
                }
                Button(
                    onClick = { isDoctorRole = true },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDoctorRole) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Врач", color = if (isDoctorRole) MedSurface else SplashBackground)
                }
            }

            if (isPending) {
                CircularProgressIndicator(color = SplashBackground)
            } else {
                Button(
                    onClick = {
                        if (password.length >= 6 && name.isNotBlank()) {
                            isPending = true
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { authResult ->
                                    val uid = authResult.user?.uid ?: ""
                                    val roleText = if (isDoctorRole) "doctor" else "patient"

                                    val userMap = mapOf(
                                        "uid" to uid,
                                        "name" to name.trim(),
                                        "email" to email,
                                        "phone" to "",
                                        "birthDate" to "",
                                        "gender" to "",
                                        "avatarUri" to "",
                                        "role" to roleText,
                                        "activeCallId" to ""
                                    )
                                    db.collection("users").document(uid).set(userMap)
                                        .addOnSuccessListener {
                                            isPending = false
                                            onRegisterSuccess()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isPending = false
                                    Toast.makeText(context, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(context, "Пароль должен быть от 6 символов!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                    shape = RoundedCornerShape(16.dp),
                    enabled = password.isNotBlank() && name.isNotBlank()
                ) {
                    Text("Зарегистрироваться", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                }
            }
        }
    }
}
