
package ru.khrom.mykursmessenger.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun LoginScreen(onAuthSuccess: (String) -> Unit, onSignUpClick: () -> Unit) { // Принимает роль в лямбду
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            Text("Вход в аккаунт", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Добро пожаловать в Skin First", fontSize = 15.sp, color = TextSecondary, fontFamily = FontFamily.Default)

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = FontFamily.Default) },
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

            if (isPending) {
                CircularProgressIndicator(color = SplashBackground)
            } else {
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isPending = true
                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnSuccessListener { authResult ->
                                    val uid = authResult.user?.uid ?: ""
                                    // Читаем роль из базы данных
                                    db.collection("users").document(uid).get()
                                        .addOnSuccessListener { snapshot ->
                                            isPending = false
                                            val role = snapshot.getString("role") ?: "patient"
                                            onAuthSuccess(role)
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isPending = false
                                    Toast.makeText(context, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Войти", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text("Еще нет аккаунта? ", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        text = "Создать",
                        color = SplashBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSignUpClick() }
                    )
                }
            }
        }
    }
}