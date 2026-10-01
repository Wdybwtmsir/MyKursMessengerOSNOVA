package ru.khrom.mykursmessenger.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorDetailsScreen(doctorId: String, onBackClick: () -> Unit, onStartChatClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()

    var docName by remember { mutableStateOf("Загрузка...") }
    var docAvatarUri by remember { mutableStateOf("") }
    var docGender by remember { mutableStateOf("Не указан") }

    val docBitmap = remember(docAvatarUri) {
        if (docAvatarUri.isNotEmpty() && docAvatarUri.contains(",")) {
            try {
                val pure = docAvatarUri.substringAfter(",")
                val bytes = Base64.decode(pure, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    // ИСПРАВЛЕНО: Заменили LaunchedEffect на DisposableEffect для полной изоляции от CoroutineScope
    DisposableEffect(doctorId) {
        val listener = if (doctorId.isNotEmpty()) {
            db.collection("users").document(doctorId).addSnapshotListener { s, _ ->
                if (s != null && s.exists()) {
                    docName = s.getString("name") ?: "Врач-Специалист"
                    docAvatarUri = s.getString("avatarUri") ?: ""
                    docGender = s.getString("gender") ?: "Не указан"
                }
            }
        } else null

        onDispose {
            listener?.remove()
        }
    }

    Scaffold(containerColor = MedBackground) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MedBackground)) {
            Row(
                modifier = Modifier.fillMaxWidth().background(MedSurface).padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary, modifier = Modifier.padding(8.dp).clickable { onBackClick() })
                Spacer(modifier = Modifier.width(12.dp))
                Text("Информация о враче", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(scrollState).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (docBitmap != null) {
                    Image(bitmap = docBitmap.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(110.dp).clip(CircleShape))
                } else {
                    Box(modifier = Modifier.size(110.dp).clip(CircleShape).background(MedPrimary.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = MedPrimary, modifier = Modifier.size(56.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = docName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Онлайн-консультант дерматологии", fontSize = 15.sp, color = MedPrimary, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("5.0", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(Проверенный профиль)", fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Пол", fontSize = 14.sp, color = TextSecondary)
                        Text(docGender, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Статус", fontSize = 14.sp, color = TextSecondary)
                        Text("В сети", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("О враче", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Данный специалист зарегистрирован в единой облачной системе SkinFirst. Готов провести онлайн-диагностику кожных заболеваний, выдать экспертные рекомендации и ответить на интересующие вопросы в чате мессенджера.",
                        fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp, textAlign = TextAlign.Justify
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(tonalElevation = 8.dp, color = MedSurface, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = { onStartChatClick() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Забронировать прием", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                    }
                }
            }
        }
    }
}
