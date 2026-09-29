package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ru.khrom.mykursmessenger.R
import ru.khrom.mykursmessenger.data.Doctor
import ru.khrom.mykursmessenger.data.Message
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun ChatScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var messages by remember { mutableStateOf(listOf<Message>()) }
    var textInput by remember { mutableStateOf("") }

    val doctors = remember {
        listOf(
            Doctor("1", "Д-р Александр Иванов", "Дерматолог", "Здравствуйте! Как ваши успехи с лечением?", "10:30", true, R.drawable.doc_alex),
            Doctor("2", "Д-р Мария Петрова", "Аллерголог", "Пришлите, пожалуйста, результаты анализов.", "Вчера", false, R.drawable.doc_maria),
            Doctor("3", "Д-р Сергей Смирнов", "Терапевт", "Жду вас на повторный прием в пятницу.", "2 дня назад", true, R.drawable.doc_sergey),
            Doctor("4", "Д-р Елена Козлова", "Педиатр", "Рецепт на лекарство я обновила.", "05.10", false, R.drawable.doc_elena)
        )
    }

    val doctor = doctors.find { it.id == doctorId } ?: doctors.first()

    DisposableEffect(doctorId) {
        val listener = db.collection("chats")
            .document(doctorId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages = snapshot.toObjects(Message::class.java)
                }
            }
        onDispose { listener.remove() }
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Image(
                        painter = painterResource(id = doctor.avatarRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = doctor.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Default
                        )
                        Text(
                            text = if (doctor.isOnline) "В сети" else "Был(а) недавно",
                            fontSize = 12.sp,
                            color = if (doctor.isOnline) Color(0xFF10B981) else TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Default
                        )
                    }
                }

                Row {
                    IconButton(onClick = { onCallClick() }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = MedPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { onVideoCallClick() }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = MedPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    val isMyMessage = message.senderId == currentUserId
                    ChatBubble(message = message, isMyMessage = isMyMessage)
                }
            }

            Surface(
                tonalElevation = 4.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Напишите сообщение...", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = MedPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                val newMessage = Message(senderId = currentUserId, text = textInput)
                                db.collection("chats")
                                    .document(doctorId)
                                    .collection("messages")
                                    .add(newMessage)
                                textInput = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MedPrimary),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = MedSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isMyMessage: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isMyMessage) BubbleOut else BubbleIn,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMyMessage) 16.dp else 2.dp,
                bottomEnd = if (isMyMessage) 2.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                color = if (isMyMessage) MedSurface else TextPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                fontSize = 15.sp,
                fontFamily = FontFamily.Default
            )
        }
    }
}
