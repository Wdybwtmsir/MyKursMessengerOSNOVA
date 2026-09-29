package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
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
fun CallScreen(doctorId: String, onDisconnectClick: () -> Unit) {
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MedSurface.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ДР",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Д-р Александр Иванов",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MedSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "02:15",
                fontSize = 16.sp,
                color = MedSurface.copy(alpha = 0.7f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { isMuted = !isMuted },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isMuted) MedSurface else MedSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    tint = if (isMuted) MedPrimary else MedSurface,
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
                onClick = { isSpeakerOn = !isSpeakerOn },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isSpeakerOn) MedSurface else MedSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = if (isSpeakerOn) MedPrimary else MedSurface,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
