package ru.khrom.mykursmessenger.presentation

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.data.CallSession
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun CallScreen(callId: String, onDisconnectClick: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var callSession by remember { mutableStateOf<CallSession?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    // Используем MediaPlayer со встроенным аудиофокусом для реальных динамиков
    val mediaPlayer = remember {
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        MediaPlayer.create(context, notificationUri).apply {
            setAudioStreamType(AudioManager.STREAM_VOICE_CALL) // Принудительный режим звонка
            isLooping = true
        }
    }

    DisposableEffect(callId) {
        val listener = db.collection("calls").document(callId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val session = snapshot.toObject(CallSession::class.java)
                    callSession = session

                    val status = session?.status ?: "ringing"
                    if (status == "ringing") {
                        try {
                            if (!mediaPlayer.isPlaying) mediaPlayer.start()
                        } catch (e: Exception) { e.printStackTrace() }
                    } else {
                        try {
                            if (mediaPlayer.isPlaying) mediaPlayer.stop()
                        } catch (e: Exception) { e.printStackTrace() }
                    }

                    if (status == "ended" || status == "rejected") {
                        db.collection("users").document(currentUserId).update("activeCallId", "")
                        try { if (mediaPlayer.isPlaying) mediaPlayer.stop() } catch (e: Exception) {}
                        onDisconnectClick()
                    }
                }
            }
        onDispose {
            listener.remove()
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            } catch (e: Exception) {}
        }
    }

    val status = callSession?.status ?: "ringing"
    val isIncoming = callSession?.receiverId == currentUserId

    Box(modifier = Modifier.fillMaxSize().background(SplashBackground), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight().padding(vertical = 60.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (status == "ringing") (if (isIncoming) "ВХОДЯЩИЙ ЗВОНОК..." else "ОЖИДАНИЕ ОТВЕТА...") else "ИДЕТ РАЗГОВОР...", fontSize = 14.sp, color = MedSurface.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Аудиосвязь SkinFirst", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MedSurface)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                IconButton(onClick = { isMuted = !isMuted }, modifier = Modifier.size(56.dp).background(if (isMuted) Color.White.copy(alpha = 0.3f) else Color.Transparent, CircleShape)) {
                    Icon(if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, null, tint = MedSurface)
                }
                IconButton(onClick = { isSpeakerOn = !isSpeakerOn }, modifier = Modifier.size(56.dp).background(if (isSpeakerOn) Color.White.copy(alpha = 0.3f) else Color.Transparent, CircleShape)) {
                    Icon(Icons.Default.VolumeUp, null, tint = MedSurface)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                if (isIncoming && status == "ringing") {
                    IconButton(onClick = { db.collection("calls").document(callId).update("status", "active") }, modifier = Modifier.size(68.dp).background(Color(0xFF10B981), CircleShape)) {
                        Icon(Icons.Default.Phone, null, tint = Color.White)
                    }
                }
                IconButton(
                    onClick = {
                        val finalStatus = if (status == "ringing") "rejected" else "ended"
                        db.collection("calls").document(callId).update("status", finalStatus).addOnSuccessListener {
                            db.collection("users").document(currentUserId).update("activeCallId", "")
                            onDisconnectClick()
                        }
                    }, modifier = Modifier.size(68.dp).background(Color(0xFFEF4444), CircleShape)
                ) { Icon(Icons.Default.CallEnd, null, tint = Color.White) }
            }
        }
    }
}
