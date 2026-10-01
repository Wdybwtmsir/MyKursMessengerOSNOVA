package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.data.Appointment
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorDashboardScreen(
    onPatientChatClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentDoctorId = auth.currentUser?.uid ?: ""

    var doctorName by remember { mutableStateOf("Врач") }
    var activePatients by remember { mutableStateOf(listOf<Appointment>()) }

    // Используем DisposableEffect для обхода зависшего кэша CoroutineScope в Android Studio
    DisposableEffect(currentDoctorId) {
        val userListener = if (currentDoctorId.isNotEmpty()) {
            db.collection("users").document(currentDoctorId).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    doctorName = snapshot.getString("name") ?: "Врач"
                }
            }
        } else null

        val appointmentsListener = if (currentDoctorId.isNotEmpty()) {
            db.collection("appointments")
                .whereEqualTo("doctorId", "1") // ID врача "1" из дефолтного репозитория
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        activePatients = snapshot.toObjects(Appointment::class.java)
                    }
                }
        } else null

        onDispose {
            userListener?.remove()
            appointmentsListener?.remove()
        }
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
                Column {
                    Text("Панель врача", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                    Text("Д-р $doctorName", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                }
                IconButton(onClick = {
                    auth.signOut()
                    onLogoutClick()
                }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFEF4444))
                }
            }

            Text(
                text = "Ваши активные пациенты на сегодня:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp),
                fontFamily = FontFamily.Default
            )

            if (activePatients.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Новых записей на прием нет", color = TextSecondary, fontFamily = FontFamily.Default)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(activePatients) { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MedSurface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(SplashBackground.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, null, tint = SplashBackground)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(app.patientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                                        Text("Время: ${app.timeSlot}", fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                                    }
                                }
                                IconButton(onClick = { onPatientChatClick(app.userId) }) {
                                    // Заменили на AutoMirrored версию, чтобы убрать предупреждение об устаревании (Deprecation)
                                    Icon(Icons.AutoMirrored.Filled.Chat, null, tint = SplashBackground, modifier = Modifier.size(26.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
