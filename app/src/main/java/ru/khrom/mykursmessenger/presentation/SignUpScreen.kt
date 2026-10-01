package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun SignUpScreen(onNextClick: (String) -> Unit, onBackToLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Scaffold(containerColor = MedBackground) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Text("Регистрация", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Шаг 1 из 2: Укажите почту", fontSize = 15.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Электронная почта (Email)", color = TextPrimary) }, // Фикс видимости лейбла
                textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    focusedLabelColor = SplashBackground, unfocusedLabelColor = TextPrimary,
                    focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface
                )
            )

            Button(
                onClick = { if (email.contains("@")) onNextClick(email.trim()) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                shape = RoundedCornerShape(16.dp), enabled = email.isNotBlank()
            ) { Text("Далее", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface) }
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text("Уже есть аккаунт? ", color = TextSecondary)
                Text("Войти", color = SplashBackground, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBackToLoginClick() })
            }
        }
    }
}
