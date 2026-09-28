package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.data.Doctor
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorListScreen(onDoctorClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    // Данные в стиле вашего медицинского UI-кита
    val doctors = remember {
        listOf(
            Doctor("1", "Д-р Александр Иванов", "Дерматолог", "Здравствуйте! Как ваши успехи с лечением?", "10:30", true),
            Doctor("2", "Д-р Мария Петрова", "Аллерголог", "Пришлите, пожалуйста, результаты анализов.", "Вчера", false),
            Doctor("3", "Д-р Сергей Смирнов", "Терапевт", "Жду вас на повторный прием в пятницу.", "2 дня назад", true),
            Doctor("4", "Д-р Елена Козлова", "Педиатр", "Рецепт на лекарство я обновила.", "05.10", false)
        )
    }

    // Фильтруем список по поисковому запросу
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
            // Кастомный надежный TopBar в стиле Figma (без использования проблемного Material3 TopAppBar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сообщения",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Строка поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск врача или специализации...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск", tint = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedPrimary,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.2f),
                    containerColor = MedSurface,
                    focusedContainerColor = MedSurface,
                    unfocusedContainerColor = MedSurface
                )
            )

            // Список диалогов
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
            // Круглая аватарка с инициалами и статусом онлайн
            Box {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MedPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = doctor.name.split(" ").getOrNull(1)?.take(1) ?: "Д",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedPrimary
                    )
                }

                // Зеленая точка онлайн-статуса
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
                                .background(Color(0xFF10B981)) // Изумрудно-зеленый
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Текстовая информация о враче и последнем сообщении
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = doctor.time, fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = doctor.specialty, fontSize = 13.sp, color = MedPrimary, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = doctor.lastMessage,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
