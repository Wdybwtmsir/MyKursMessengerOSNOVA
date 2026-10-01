package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.data.CalendarDay // ИМПОРТ ИЗ ПАПКИ DATA
import ru.khrom.mykursmessenger.data.Doctor
import ru.khrom.mykursmessenger.data.DoctorRepository
import ru.khrom.mykursmessenger.ui.theme.*

data class TimeSlot(val time: String, val isAvailable: Boolean, val isSelected: Boolean = false)

@Composable
fun ScheduleScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    onBookClick: (date: String, slot: String, forWhom: String, problem: String) -> Unit
) {
    val scrollState = rememberScrollState()

    var selectedDateTab by remember { mutableIntStateOf(2) }
    var selectedTimeSlot by remember { mutableStateOf("") }
    var isForYourself by remember { mutableStateOf(true) }
    var noteText by remember { mutableStateOf("") }

    val dates = remember {
        listOf(
            CalendarDay("22", "MON"),
            CalendarDay("23", "TUE"),
            CalendarDay("24", "WED", true),
            CalendarDay("25", "THU"),
            CalendarDay("26", "FRI"),
            CalendarDay("27", "SAT")
        )
    }

    val timeSlots = remember(selectedTimeSlot) {
        listOf(
            TimeSlot("9:00 AM", true), TimeSlot("9:30 AM", true), TimeSlot("10:00 AM", true),
            TimeSlot("10:30 AM", false), TimeSlot("11:00 AM", true), TimeSlot("11:30 AM", false),
            TimeSlot("12:00 PM", false), TimeSlot("12:30 PM", true), TimeSlot("1:00 PM", true),
            TimeSlot("1:30 PM", true), TimeSlot("2:00 PM", true), TimeSlot("2:30 PM", true)
        ).map { it.copy(isSelected = it.time == selectedTimeSlot) }
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
                    text = "Schedule",
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
                Spacer(modifier = Modifier.height(20.dp))

                Text("Select Date", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dates.forEachIndexed { index, day: CalendarDay ->
                        val isSelected = selectedDateTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) SplashBackground else MedSurface)
                                .clickable { selectedDateTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(day.day, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MedSurface else TextPrimary)
                                Text(day.name, fontSize = 10.sp, color = if (isSelected) MedSurface.copy(alpha = 0.8f) else TextSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Available Time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.height(160.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = timeSlots) { slot: TimeSlot ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (slot.isSelected) SplashBackground
                                        else if (slot.isAvailable) Color(0xFFD6E4FF).copy(alpha = 0.5f)
                                        else MedBackground
                                    )
                                    .border(
                                        1.dp,
                                        if (slot.isSelected) SplashBackground else Color(0xFFD6E4FF),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable(enabled = slot.isAvailable) { selectedTimeSlot = slot.time },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = slot.time,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (slot.isSelected) MedSurface else if (slot.isAvailable) SplashBackground else TextSecondary.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Patient Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { isForYourself = true },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isForYourself) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text("Yourself", color = if (isForYourself) MedSurface else SplashBackground, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { isForYourself = false },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isForYourself) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text("Another Person", color = if (!isForYourself) MedSurface else SplashBackground, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Describe your problem", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Enter Your Problem Here...", color = TextSecondary) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = MedPrimary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )
            }
            Surface(
                tonalElevation = 8.dp,
                color = MedSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = {
                            if (selectedTimeSlot.isNotEmpty()) {
                                val selectedDate = "September ${dates[selectedDateTab].day}, 2026 | ${dates[selectedDateTab].name}"
                                val bookingFor = if (isForYourself) "Yourself" else "Another Person"
                                onBookClick(selectedDate, selectedTimeSlot, bookingFor, noteText.ifBlank { "No description provided." })
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                        shape = RoundedCornerShape(16.dp),
                        enabled = selectedTimeSlot.isNotEmpty()
                    ) {
                        Text("Book Appointment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                    }
                }
            }
        }
    }
}