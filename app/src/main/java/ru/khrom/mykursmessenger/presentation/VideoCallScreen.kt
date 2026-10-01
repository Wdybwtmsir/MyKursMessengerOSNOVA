package ru.khrom.mykursmessenger.presentation

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.data.CallSession

@Composable
fun VideoCallScreen(callId: String, onDisconnectClick: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var callSession by remember { mutableStateOf<CallSession?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "camera")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val mediaPlayer = remember {
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        MediaPlayer.create(context, notificationUri).apply {
            setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight().padding(vertical = 60.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (status == "ringing") (if (isIncoming) "ВХОДЯЩИЙ ВИДЕОВЫЗОВ..." else "ОЖИДАНИЕ КАМЕРЫ...") else "ВИДЕОКОНФЕРЕНЦИЯ...", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Видеосвязь SkinFirst", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(if (status == "active") scale else 1f)
                    .background(if (status == "active") Color(0xFF10B981).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Videocam, null, tint = if (status == "active") Color(0xFF10B981) else Color.White, modifier = Modifier.size(56.dp))
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
