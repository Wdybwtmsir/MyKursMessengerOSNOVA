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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ru.khrom.mykursmessenger.data.CallSession
import ru.khrom.mykursmessenger.data.Message
import ru.khrom.mykursmessenger.ui.theme.*
import java.util.Date
import java.util.UUID

@Composable
fun ChatScreen(doctorId: String, onBackClick: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    var targetUserName by remember { mutableStateOf("Собеседник") }
    var messageText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(listOf<Message>()) }

    val chatId = remember(currentUserId, doctorId) {
        if (currentUserId < doctorId) "${currentUserId}_$doctorId" else "${doctorId}_$currentUserId"
    }

    DisposableEffect(chatId) {
        // Подгружаем имя собеседника из базы (будь то врач или пациент)
        db.collection("users").document(doctorId).get().addOnSuccessListener { snap ->
            if (snap.exists()) {
                targetUserName = snap.getString("name") ?: "Пользователь"
            }
        }

        val listener = db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chatMessages = snapshot.toObjects(Message::class.java)
                }
            }
        onDispose { listener.remove() }
    }

    // Инициация звонка (Вызывающий сразу прописывает звонок и СЕБЕ, и ПОЛУЧАТЕЛЮ)
    fun startFirebaseCall(isVideo: Boolean) {
        val uniqueCallId = UUID.randomUUID().toString()
        val session = CallSession(
            callId = uniqueCallId, callerId = currentUserId,
            callerName = "SkinFirst Пользователь", receiverId = doctorId,
            type = if (isVideo) "video" else "voice", status = "ringing"
        )

        // Создаем сессию звонка в облаке
        db.collection("calls").document(uniqueCallId).set(session).addOnSuccessListener {
            // Пишем activeCallId обоим участникам, чтобы у обоих открылось окно вызова!
            db.collection("users").document(doctorId).update("activeCallId", uniqueCallId)
            db.collection("users").document(currentUserId).update("activeCallId", uniqueCallId)
        }
    }

    Scaffold(
        containerColor = MedBackground,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(MedSurface).padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextPrimary, modifier = Modifier.padding(8.dp).clickable { onBackClick() })
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.LightGray)) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.fillMaxSize().padding(4.dp), tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(targetUserName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground)
                    Text("Консультация онлайн", fontSize = 12.sp, color = Color(0xFF10B981))
                }
                IconButton(onClick = { startFirebaseCall(isVideo = false) }) { Icon(Icons.Default.Call, null, tint = SplashBackground) }
                IconButton(onClick = { startFirebaseCall(isVideo = true) }) { Icon(Icons.Default.Videocam, null, tint = SplashBackground) }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(chatMessages) { msg ->
                    val isMyMessage = msg.senderId == currentUserId
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isMyMessage) SplashBackground else MedSurface),
                            shape = RoundedCornerShape(16.dp), modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(text = msg.text, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), color = if (isMyMessage) MedSurface else TextPrimary, fontSize = 15.sp)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().background(MedSurface).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = messageText, onValueChange = { messageText = it },
                    placeholder = { Text("Введите сообщение...", color = TextSecondary) },
                    textStyle = TextStyle(color = TextPrimary),
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MedBackground, unfocusedContainerColor = MedBackground, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val chatData = mapOf("participants" to listOf(currentUserId, doctorId))
                            db.collection("chats").document(chatId).set(chatData, com.google.firebase.firestore.SetOptions.merge())
                                .addOnSuccessListener {
                                    val newMessage = Message(senderId = currentUserId, text = messageText.trim(), timestamp = Date())
                                    db.collection("chats").document(chatId).collection("messages").add(newMessage)
                                    messageText = ""
                                }
                        }
                    }, modifier = Modifier.size(44.dp).background(SplashBackground, CircleShape)
                ) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = MedSurface) }
            }
        }
    }
}
