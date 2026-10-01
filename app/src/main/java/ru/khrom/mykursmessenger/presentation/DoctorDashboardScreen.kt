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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.data.User
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorDashboardScreen(
    onPatientChatClick: (String) -> Unit,
    onProfileClick: () -> Unit, // Лямбда перехода в личные данные
    onLogoutClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentDoctorId = auth.currentUser?.uid ?: ""

    var doctorName by remember { mutableStateOf("Врач") }
    var doctorAvatarUrl by remember { mutableStateOf("") }
    var activePatients by remember { mutableStateOf(listOf<User>()) }

    val doctorBitmap = remember(doctorAvatarUrl) {
        if (doctorAvatarUrl.isNotEmpty() && doctorAvatarUrl.contains(",")) {
            try {
                val pureBytes = doctorAvatarUrl.substringAfter(",")
                val bytes = Base64.decode(pureBytes, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    DisposableEffect(currentDoctorId) {
        val userListener = if (currentDoctorId.isNotEmpty()) {
            db.collection("users").document(currentDoctorId).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    doctorName = snapshot.getString("name") ?: "Врач"
                    doctorAvatarUrl = snapshot.getString("avatarUri") ?: ""
                }
            }
        } else null

        val chatsListener = db.collection("chats")
            .whereArrayContains("participants", currentDoctorId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val patientIds = snapshot.documents.mapNotNull { doc ->
                        val participants = doc.get("participants") as? List<*>
                        participants?.firstOrNull { it != currentDoctorId } as? String
                    }.distinct()

                    if (patientIds.isEmpty()) {
                        activePatients = emptyList()
                    } else {
                        db.collection("users").whereIn("uid", patientIds).get()
                            .addOnSuccessListener { usersSnap ->
                                activePatients = usersSnap.toObjects(User::class.java)
                            }
                    }
                }
            }

        onDispose {
            userListener?.remove()
            chatsListener?.remove()
        }
    }

    Scaffold(containerColor = MedBackground) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onProfileClick() }) {
                    if (doctorBitmap != null) {
                        Image(bitmap = doctorBitmap.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(52.dp).clip(CircleShape))
                    } else {
                        Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(SplashBackground.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = SplashBackground)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Панель врача", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Д-р $doctorName (Настройки ⚙️)", fontSize = 13.sp, color = SplashBackground, fontWeight = FontWeight.Medium)
                    }
                }
                IconButton(onClick = { auth.signOut(); onLogoutClick() }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFEF4444))
                }
            }

            Text("Ваши активные чаты с пациентами:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(bottom = 12.dp))

            if (activePatients.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("У вас пока нет активных диалогов", color = TextSecondary)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(activePatients) { patient ->
                        val patientBitmap = remember(patient.avatarUri) {
                            if (patient.avatarUri.isNotEmpty() && patient.avatarUri.contains(",")) {
                                try {
                                    val pureBytes = patient.avatarUri.substringAfter(",")
                                    val bytes = Base64.decode(pureBytes, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (e: Exception) { null }
                            } else null
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MedSurface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (patientBitmap != null) {
                                        Image(bitmap = patientBitmap.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(48.dp).clip(CircleShape))
                                    } else {
                                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(SplashBackground.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Person, null, tint = SplashBackground)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(patient.name.ifBlank { "Пациент" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(patient.email, fontSize = 13.sp, color = TextSecondary)
                                    }
                                }
                                IconButton(onClick = { onPatientChatClick(patient.uid) }) {
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