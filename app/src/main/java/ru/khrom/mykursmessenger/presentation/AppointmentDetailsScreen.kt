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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.khrom.mykursmessenger.data.DoctorRepository
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun AppointmentDetailsScreen(
    doctorId: String,
    dateText: String,
    timeSlot: String,
    bookingFor: String,
    problemDescription: String,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
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
                    text = "Your Appointment",
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

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SplashBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dateText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedSurface,
                            fontFamily = FontFamily.Default
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timeSlot,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MedSurface.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Default
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Booking For", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    Text(bookingFor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Full Name", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    Text(if (bookingFor == "Yourself") "User Profile Name" else "Jane Doe", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Age", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    Text(if (bookingFor == "Yourself") "—" else "30", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gender", fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    Text(if (bookingFor == "Yourself") "—" else "Female", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFD6E4FF).copy(alpha = 0.4f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                Text("Problem", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = problemDescription,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(
                tonalElevation = 8.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = { onConfirmClick() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Confirm Appointment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                    }
                }
            }
        }
    }
}
