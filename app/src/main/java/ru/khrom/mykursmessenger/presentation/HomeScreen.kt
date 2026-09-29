package ru.khrom.mykursmessenger.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.R
import ru.khrom.mykursmessenger.data.Doctor
import ru.khrom.mykursmessenger.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarDay(
    val day: String,
    val name: String,
    val isSelected: Boolean = false
)

@Composable
fun HomeScreen(onDoctorClick: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val userEmail = auth.currentUser?.email ?: "example@mail.com"

    var userName by remember { mutableStateOf("Пациент") }
    var userAvatarUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val nameFromDb = snapshot.getString("name")
                    userName = if (!nameFromDb.isNullOrEmpty()) nameFromDb else userEmail.substringBefore("@")

                    val uriStr = snapshot.getString("avatarUri")
                    userAvatarUri = if (!uriStr.isNullOrEmpty()) uriStr.toUri() else null
                } else {
                    userName = userEmail.substringBefore("@")
                }
            }
        }
    }

    val days = remember {
        val calendar = Calendar.getInstance()
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dayList = mutableListOf<CalendarDay>()
        val dayNameFormat = SimpleDateFormat("EEE", Locale.US)

        for (i in 0..5) {
            val dayNum = calendar.get(Calendar.DAY_OF_MONTH)
            val dayName = dayNameFormat.format(calendar.time).uppercase()

            dayList.add(
                CalendarDay(
                    day = dayNum.toString(),
                    name = dayName,
                    isSelected = dayNum == currentDayOfMonth
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        dayList
    }

    val todayText = remember {
        val calendar = Calendar.getInstance()
        val dayNum = calendar.get(Calendar.DAY_OF_MONTH)
        val dayNameFormat = SimpleDateFormat("EEEE", Locale.US)
        val dayName = dayNameFormat.format(calendar.time)
        "$dayNum $dayName - Today"
    }

    val doctors = remember {
        listOf(
            Doctor("1", "Д-р Александр Иванов", "Дерматолог", "Здравствуйте! Как ваши успехи с лечением?", "10:30", true, R.drawable.doc_alex),
            Doctor("2", "Д-р Мария Петрова", "Аллерголог", "Пришлите, пожалуйста, результаты анализов.", "Вчера", false, R.drawable.doc_maria),
            Doctor("3", "Д-р Сергей Смирнов", "Терапевт", "Жду вас на повторный прием в пятницу.", "2 дня назад", true, R.drawable.doc_sergey),
            Doctor("4", "Д-р Елена Козлова", "Педиатр", "Рецепт на лекарство я обновила.", "05.10", false, R.drawable.doc_elena)
        )
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC6D6FF).copy(alpha = 0.5f))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (userAvatarUri != null) {
                            AsyncImage(
                                model = userAvatarUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MedPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.take(1).uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedPrimary,
                                    fontFamily = FontFamily.Default
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Hi, WelcomeBack",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Default
                            )
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                    Row {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = SplashBackground,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = SplashBackground,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, tint = SplashBackground, modifier = Modifier.size(24.dp))
                        Text("Doctors", fontSize = 12.sp, color = SplashBackground, fontFamily = FontFamily.Default)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                        Text("Favorite", fontSize = 12.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(MedSurface, RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Default.Tune, null, tint = TextSecondary)
                        Icon(Icons.Default.Search, null, tint = SplashBackground)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(days) { item ->
                        Box(
                            modifier = Modifier
                                .size(width = 54.dp, height = 76.dp)
                                .clip(RoundedCornerShape(27.dp))
                                .background(if (item.isSelected) SplashBackground else MedSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.day,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isSelected) MedSurface else TextPrimary,
                                    fontFamily = FontFamily.Default
                                )
                                Text(
                                    text = item.name,
                                    fontSize = 11.sp,
                                    color = if (item.isSelected) MedSurface.copy(alpha = 0.7f) else TextSecondary,
                                    fontFamily = FontFamily.Default
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = todayText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SplashBackground,
                        fontFamily = FontFamily.Default
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedSurface.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("9 AM", fontSize = 12.sp, color = TextSecondary)
                            Text("....................................................................", color = TextSecondary.copy(alpha = 0.3f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("10 AM", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E4FF))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Д-р Александр Иванов", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SplashBackground)
                                    Text("Treatment and prevention of skin and photodermatitis.", fontSize = 12.sp, color = TextPrimary)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("11 AM", fontSize = 12.sp, color = TextSecondary)
                            Text("....................................................................", color = TextSecondary.copy(alpha = 0.3f))
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(doctors) { doc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDoctorClick(doc.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E4FF).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = doc.avatarRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(doc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                                Text(doc.specialty, fontSize = 12.sp, color = TextSecondary, fontFamily = FontFamily.Default)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("4.9", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Q 60", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
