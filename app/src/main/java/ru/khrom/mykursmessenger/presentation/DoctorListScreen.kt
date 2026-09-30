package ru.khrom.mykursmessenger.presentation

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
import ru.khrom.mykursmessenger.data.Doctor
import ru.khrom.mykursmessenger.data.DoctorRepository
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun DoctorListScreen(onDoctorClick: (String) -> Unit, onProfileClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenderFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Male", "Female")

    // Чистый вызов глобального репозитория сетевых ссылок
    val doctors = remember { DoctorRepository.doctors }

    val filteredDoctors = doctors.filter { doctor ->
        val matchesSearch = doctor.name.contains(searchQuery, ignoreCase = true) || doctor.specialty.contains(searchQuery, ignoreCase = true)
        val matchesGender = selectedGenderFilter == "All" || doctor.gender.equals(selectedGenderFilter, ignoreCase = true)
        matchesSearch && matchesGender
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
                    text = "Doctors",
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filterTitle ->
                    val isSelected = selectedGenderFilter == filterTitle
                    Button(
                        onClick = { selectedGenderFilter = filterTitle },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(19.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = filterTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MedSurface else SplashBackground,
                            fontFamily = FontFamily.Default
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredDoctors) { doctor ->
                    DoctorCardItem(doctor = doctor, onClick = { onDoctorClick(doctor.id) })
                }
            }
        }
    }
}

@Composable
fun DoctorCardItem(doctor: Doctor, onClick: () -> Unit) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = doctor.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
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
                    Text(text = doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = doctor.specialty, fontSize = 13.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("4.9", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Q 60", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }
    }
}
