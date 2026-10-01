package ru.khrom.mykursmessenger.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.data.User // Используем общую модель User
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorListScreen(
    onDoctorClick: (String) -> Unit,
    onChatClick: (String) -> Unit, // Лямбда для мгновенного перехода в чат
    onProfileClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenderFilter by remember { mutableStateOf("Все") }
    val filters = listOf("Все", "Мужской", "Женский")

    var dbDoctors by remember { mutableStateOf(listOf<User>()) }

    // В реальном времени подгружаем всех врачей из Firebase Firestore
    DisposableEffect(Unit) {
        val listener = db.collection("users")
            .whereEqualTo("role", "doctor")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    dbDoctors = snapshot.toObjects(User::class.java)
                }
            }
        onDispose { listener.remove() }
    }

    val filteredDoctors = dbDoctors.filter { doc ->
        val matchesSearch = doc.name.contains(searchQuery, ignoreCase = true)
        val matchesGender = when (selectedGenderFilter) {
            "Мужской" -> doc.gender.equals("Мужской", ignoreCase = true) || doc.gender.equals("Male", ignoreCase = true)
            "Женский" -> doc.gender.equals("Женский", ignoreCase = true) || doc.gender.equals("Female", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesGender
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Наши Врачи",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                IconButton(onClick = { onProfileClick() }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MedPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск врача по имени...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    focusedLabelColor = SplashBackground, unfocusedLabelColor = TextPrimary,
                    focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filterTitle ->
                    val isSelected = selectedGenderFilter == filterTitle
                    Button(
                        onClick = { selectedGenderFilter = filterTitle },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(19.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = filterTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MedSurface else SplashBackground
                        )
                    }
                }
            }

            if (filteredDoctors.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Врачи не найдены", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        DoctorFirebaseCardItem(
                            doctor = doctor,
                            onClick = { onDoctorClick(doctor.uid) },
                            onChatInstantClick = { onChatClick(doctor.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorFirebaseCardItem(doctor: User, onClick: () -> Unit, onChatInstantClick: () -> Unit) {
    val avatarBitmap = remember(doctor.avatarUri) {
        if (doctor.avatarUri.isNotEmpty() && doctor.avatarUri.contains(",")) {
            try {
                val pureBase64 = doctor.avatarUri.substringAfter(",")
                val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MedPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = MedPrimary)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(text = doctor.name.ifBlank { "Д-р Специалист" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground)
                    Text(text = "Онлайн-консультант", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("5.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            // Кнопка мгновенного перехода в чат слёту
            IconButton(
                onClick = { onChatInstantClick() },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFD6E4FF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = SplashBackground
                )
            }
        }
    }
}