package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import ru.khrom.mykursmessenger.data.DoctorRepository
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun ReviewSummaryScreen(
    doctorId: String,
    dateText: String,
    timeSlot: String,
    bookingFor: String,
    onBackClick: () -> Unit,
    onPayNowClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val doctor = remember(doctorId) {
        DoctorRepository.doctors.find { it.id == doctorId } ?: DoctorRepository.doctors.first()
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
                    text = "Review Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = doctor.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                            Text(doctor.specialty, fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("5.0", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Date & Time", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Text("$dateText | $timeSlot", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Booking For", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Text(bookingFor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Consultation Fee", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Text("$100.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, fontFamily = FontFamily.Default)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Admin Fee", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Text("$5.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, fontFamily = FontFamily.Default)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tax (5%)", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Text("$5.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, fontFamily = FontFamily.Default)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount / Promo", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                            Text("-$10.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF10B981), fontFamily = FontFamily.Default)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFD6E4FF).copy(alpha = 0.4f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total Payable", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                            Text("$100.00", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(
                tonalElevation = 8.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = { onPayNowClick() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Pay Now", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                    }
                }
            }
        }
    }
}
