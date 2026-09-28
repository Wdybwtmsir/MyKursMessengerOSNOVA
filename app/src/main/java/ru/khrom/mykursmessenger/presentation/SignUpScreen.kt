package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun SignUpScreen(
    onNextClick: (String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Верхняя часть: Заголовок из Figma
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Создать аккаунт",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Заполните данные, чтобы зарегистрироваться в системе",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Средняя часть: Поля ввода
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Полное имя") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                )
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 13.sp
                )
            }
        }

        // Нижняя часть: Кнопка «Продолжить» и возврат
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (email.isNotEmpty() && fullName.isNotEmpty()) {
                        onNextClick(email) // Передаем email на следующий шаг установки пароля
                    } else {
                        errorMessage = "Пожалуйста, заполните все поля"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Продолжить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MedSurface
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(text = "Уже есть аккаунт? ", color = TextSecondary, fontSize = 14.sp)
                Text(
                    text = "Войти",
                    color = MedPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBackToLoginClick() }
                )
            }
        }
    }
}
