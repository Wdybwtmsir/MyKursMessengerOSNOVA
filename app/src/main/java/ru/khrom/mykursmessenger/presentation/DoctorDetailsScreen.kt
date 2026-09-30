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
import androidx.compose.runtime.*
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
fun DoctorDetailsScreen(doctorId: String, onBackClick: () -> Unit, onStartChatClick: () -> Unit) {
    val scrollState = rememberScrollState()

    // Берем данные строго из сетевого репозитория DoctorRepository
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
                .background(MedBackground)
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
                    text = "Информация о враче",
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Заменили Image на AsyncImage для сетевой аватарки URL
                AsyncImage(
                    model = doctor.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = doctor.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = doctor.specialty,
                    fontSize = 15.sp,
                    color = MedPrimary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.9",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Default
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(120 отзывов)",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Default
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("150+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        Text("Пациентов", fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("10 лет", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        Text("Опыт работы", fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("40+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        Text("Обзоров", fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "О враче",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Default
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Доктор является высококвалифицированным специалистом в своей области с многолетним опытом диагностики и лечения сложных клинических случаев. Регулярно участвует в международных медицинских конференциях и использует передовые методы лечения.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Default,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Surface(
                tonalElevation = 8.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { onStartChatClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Book Appointment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedSurface,
                            fontFamily = FontFamily.Default
                        )
                    }
                }
            }
        }
    }
}
