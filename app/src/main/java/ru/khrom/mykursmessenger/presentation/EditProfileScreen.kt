package ru.khrom.mykursmessenger.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun EditProfileScreen(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var birthDateInput by remember { mutableStateOf("") }
    var genderInput by remember { mutableStateOf("") }
    var selectedAvatarUrl by remember { mutableStateOf("") }
    var isPending by remember { mutableStateOf(false) }

    // Готовый набор медицинских аватарок из интернета, чтобы не использовать платный Storage
    val predefinedAvatars = listOf(
        "https://unsplash.com", // Девушка
        "https://unsplash.com", // Парень
        "https://unsplash.com", // Женщина
        "https://unsplash.com"  // Мужчина
    )

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot != null && snapshot.exists()) {
                    nameInput = snapshot.getString("name") ?: ""
                    phoneInput = snapshot.getString("phone") ?: ""
                    birthDateInput = snapshot.getString("birthDate") ?: ""
                    genderInput = snapshot.getString("gender") ?: ""
                    selectedAvatarUrl = snapshot.getString("avatarUri") ?: ""
                }
            }
        }
    }

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
                    text = "Edit Profile",
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Главное выбранное изображение профиля
                if (selectedAvatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = selectedAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MedPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = MedPrimary, modifier = Modifier.size(48.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Choose Your Avatar", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                // Горизонтальная лента для выбора готовой аватарки кликом (без Storage!)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(predefinedAvatars) { url ->
                        val isSelected = selectedAvatarUrl == url
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) SplashBackground else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatarUrl = url }
                        )
                    }
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Full Name", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface)
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Phone Number", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface)
                )

                OutlinedTextField(
                    value = birthDateInput,
                    onValueChange = { birthDateInput = it },
                    label = { Text("Birth Date (DD/MM/YYYY)", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface)
                )

                OutlinedTextField(
                    value = genderInput,
                    onValueChange = { genderInput = it },
                    label = { Text("Gender", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface)
                )
            }

            Surface(
                tonalElevation = 8.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPending) {
                        CircularProgressIndicator(color = SplashBackground)
                    } else {
                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    isPending = true
                                    val updates = mapOf(
                                        "name" to nameInput.trim(),
                                        "phone" to phoneInput.trim(),
                                        "birthDate" to birthDateInput.trim(),
                                        "gender" to genderInput.trim(),
                                        "avatarUri" to selectedAvatarUrl.trim()
                                    )
                                    db.collection("users").document(userId).set(updates, com.google.firebase.firestore.SetOptions.merge())
                                        .addOnSuccessListener {
                                            isPending = false
                                            onSaveClick()
                                        }
                                        .addOnFailureListener {
                                            isPending = false
                                            Toast.makeText(context, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                            shape = RoundedCornerShape(16.dp),
                            enabled = nameInput.isNotBlank()
                        ) {
                            Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                        }
                    }
                }
            }
        }
    }
}
