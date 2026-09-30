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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.*

data class NotificationItem(
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val isUnread: Boolean = false
)

@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    val todayNotifications = listOf(
        NotificationItem("Scheduled Appointment", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "2 M", Icons.Default.CalendarMonth, false),
        NotificationItem("Scheduled Change", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "2 H", Icons.Default.EditCalendar, true),
        NotificationItem("Medical Notes", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "3 H", Icons.Default.MedicalInformation, false)
    )

    val yesterdayNotifications = listOf(
        NotificationItem("Scheduled Appointment", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "1 D", Icons.Default.CalendarMonth, false)
    )

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
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Notification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text("Today", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                }

                items(todayNotifications) { item ->
                    NotificationCard(item)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Yesterday", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Default)
                }

                items(yesterdayNotifications) { item ->
                    NotificationCard(item)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isUnread) Color(0xFFD6E4FF).copy(alpha = 0.4f) else MedSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SplashBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = null, tint = MedSurface, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                    Text(item.time, fontSize = 12.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}
