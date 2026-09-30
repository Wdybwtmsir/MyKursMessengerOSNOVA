package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ru.khrom.mykursmessenger.data.DoctorRepository
import ru.khrom.mykursmessenger.data.Message
import ru.khrom.mykursmessenger.ui.theme.*
import java.util.Date

@Composable
fun ChatScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    val doctor = remember(doctorId) {
        DoctorRepository.doctors.find { it.id == doctorId } ?: DoctorRepository.doctors.first()
    }

    var messageText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(listOf<Message>()) }

    val chatId = remember(currentUserId, doctorId) {
        if (currentUserId < doctorId) "${currentUserId}_$doctorId" else "${doctorId}_$currentUserId"
    }

    LaunchedEffect(chatId) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chatMessages = snapshot.toObjects(Message::class.java)
                }
            }
    }

    Scaffold(
        containerColor = MedBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedSurface)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.width(4.dp))

                AsyncImage(
                    model = doctor.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                    Text(if (doctor.isOnline) "В сети" else "Вне сети", fontSize = 12.sp, color = if (doctor.isOnline) Color(0xFF10B981) else TextSecondary)
                }

                IconButton(onClick = onCallClick) {
                    Icon(Icons.Default.Call, null, tint = SplashBackground)
                }
                IconButton(onClick = onVideoCallClick) {
                    Icon(Icons.Default.Videocam, null, tint = SplashBackground)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(chatMessages) { msg ->
                    val isMyMessage = msg.senderId == currentUserId
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMyMessage) SplashBackground else MedSurface
                            ),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMyMessage) 16.dp else 0.dp,
                                bottomEnd = if (isMyMessage) 0.dp else 16.dp
                            ),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                color = if (isMyMessage) MedSurface else TextPrimary,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Введите сообщение...", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MedBackground,
                        unfocusedContainerColor = MedBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            // Передаем объект Date(System.currentTimeMillis()) для соответствия вашей модели данных
                            val newMessage = Message(
                                senderId = currentUserId,
                                text = messageText.trim(),
                                timestamp = Date(System.currentTimeMillis())
                            )
                            db.collection("chats")
                                .document(chatId)
                                .collection("messages")
                                .add(newMessage)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(SplashBackground, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = MedSurface)
                }
            }
        }
    }
}
