package ru.khrom.mykursmessenger.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun PasswordManagerScreen(onBackClick: () -> Unit, onChangePasswordSuccess: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPending by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedSurface)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(enabled = !isPending) { onBackClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Password Manager",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password", fontFamily = FontFamily.Default) },
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = MedPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedBorderColor = MedPrimary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password", fontFamily = FontFamily.Default) },
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = MedPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedBorderColor = MedPrimary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password", fontFamily = FontFamily.Default) },
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = MedPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedBorderColor = MedPrimary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )
            }

            Surface(
                tonalElevation = 8.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPending) {
                        CircularProgressIndicator(color = SplashBackground)
                    } else {
                        Button(
                            onClick = {
                                if (user != null && user.email != null) {
                                    if (newPassword.length < 6) {
                                        Toast.makeText(context, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isPending = true

                                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                                    user.reauthenticate(credential)
                                        .addOnSuccessListener {
                                            user.updatePassword(newPassword)
                                                .addOnSuccessListener {
                                                    isPending = false
                                                    Toast.makeText(context, "Password Changed Successfully!", Toast.LENGTH_SHORT).show()
                                                    onChangePasswordSuccess()
                                                }
                                                .addOnFailureListener { e ->
                                                    isPending = false
                                                    Toast.makeText(context, "Ошибка изменения: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            isPending = false
                                            Toast.makeText(context, "Текущий пароль неверен", Toast.LENGTH_LONG).show()
                                        }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                            shape = RoundedCornerShape(16.dp),
                            enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == confirmPassword
                        ) {
                            Text(
                                text = "Change Password",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedSurface,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
            }
        }
    }
}
