package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun VideoCallScreen(doctorId: String, onDisconnectClick: () -> Unit) {
    var isVideoOn by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isVideoOn) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Камера врача (Видеопоток)",
                        color = MedSurface.copy(alpha = 0.6f),
                        fontSize = 18.sp
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MedPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ДР", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MedPrimary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Видео отключено доктором", color = MedSurface, fontSize = 16.sp)
                }
            }
        }

        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 160.dp)
                .padding(top = 40.dp, end = 24.dp)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF475569)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Вы",
                color = MedSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Д-р Александр Иванов",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MedSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "05:23",
                fontSize = 14.sp,
                color = MedSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isVideoOn = !isVideoOn },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isVideoOn) MedSurface.copy(alpha = 0.2f) else MedSurface
                    ),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = null,
                        tint = if (isVideoOn) MedSurface else MedPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(
                    onClick = { onDisconnectClick() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = null,
                        tint = MedSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MedSurface.copy(alpha = 0.2f)),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwitchVideo,
                        contentDescription = null,
                        tint = MedSurface,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
