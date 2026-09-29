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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import ru.khrom.mykursmessenger.R
import ru.khrom.mykursmessenger.data.Doctor
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorListScreen(onDoctorClick: (String) -> Unit, onProfileClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    val doctors = remember {
        listOf(
            Doctor("1", "Д-р Александр Иванов", "Дерматолог", "Здравствуйте! Как ваши успехи с лечением?", "10:30", true, R.drawable.doc_alex),
            Doctor("2", "Д-р Мария Петрова", "Аллерголог", "Пришлите, пожалуйста, результаты анализов.", "Вчера", false, R.drawable.doc_maria),
            Doctor("3", "Д-р Сергей Смирнов", "Терапевт", "Жду вас на повторный прием в пятницу.", "2 дня назад", true, R.drawable.doc_sergey),
            Doctor("4", "Д-р Елена Козлова", "Педиатр", "Рецепт на лекарство я обновила.", "05.10", false, R.drawable.doc_elena)
        )
    }

    val filteredDoctors = doctors.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.specialty.contains(searchQuery, ignoreCase = true)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Сообщения",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )

                IconButton(onClick = { onProfileClick() }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MedPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск врача или специализации...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = MedPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredDoctors) { doctor ->
                    DoctorItem(doctor = doctor, onClick = { onDoctorClick(doctor.id) })
                }
            }
        }
    }
}

@Composable
fun DoctorItem(doctor: Doctor, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    painter = painterResource(id = doctor.avatarRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )

                if (doctor.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                    Text(text = doctor.time, fontSize = 12.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = doctor.specialty, fontSize = 13.sp, color = MedPrimary, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = doctor.lastMessage,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}
