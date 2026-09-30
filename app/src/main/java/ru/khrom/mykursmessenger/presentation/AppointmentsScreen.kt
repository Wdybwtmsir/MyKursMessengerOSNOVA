package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import ru.khrom.mykursmessenger.data.Appointment
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun AppointmentsScreen(onReviewClick: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Complete", "Upcoming", "Cancelled")

    var cloudAppointments by remember { mutableStateOf(listOf<Appointment>()) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            db.collection("appointments")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        cloudAppointments = snapshot.toObjects(Appointment::class.java)
                    }
                }
        }
    }

    val filteredAppointments = cloudAppointments.filter { app ->
        val currentTabTitle = tabs[selectedTab]
        app.status.equals(currentTabTitle, ignoreCase = true)
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("All Appointment", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Button(
                        onClick = { selectedTab = index },
                        modifier = Modifier.weight(1f).height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(19.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (isSelected) MedSurface else SplashBackground, fontFamily = FontFamily.Default)
                    }
                }
            }

            if (filteredAppointments.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No appointments found", color = TextSecondary, fontSize = 14.sp, fontFamily = FontFamily.Default)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredAppointments) { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MedSurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = app.doctorAvatarUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(56.dp).clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(app.doctorName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                                        Text(app.doctorSpecialty, fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("5.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "${app.dateText} | ${app.timeSlot}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SplashBackground,
                                    modifier = Modifier.background(Color(0xFFD6E4FF).copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(22.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SplashBackground),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, SplashBackground)
                                    ) {
                                        Text("Re-Book", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Button(
                                        onClick = { onReviewClick(app.doctorId) },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(22.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SplashBackground)
                                    ) {
                                        Text("Add Review", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MedSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}