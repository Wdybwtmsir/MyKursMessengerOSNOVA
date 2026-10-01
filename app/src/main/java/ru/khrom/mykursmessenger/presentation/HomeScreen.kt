package ru.khrom.mykursmessenger.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.asImageBitmap
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
import ru.khrom.mykursmessenger.data.CalendarDay
import ru.khrom.mykursmessenger.data.User // Используем модель User вместо хардкода
import ru.khrom.mykursmessenger.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    onDoctorClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDoctorsTabClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val userEmail = auth.currentUser?.email ?: "example@mail.com"

    var userName by remember { mutableStateOf("Пациент") }
    var userAvatarUrl by remember { mutableStateOf("") }

    var homeSearchQuery by remember { mutableStateOf("") }
    var dbDoctors by remember { mutableStateOf(listOf<User>()) }

    val avatarBitmap = remember(userAvatarUrl) {
        if (userAvatarUrl.isNotEmpty() && userAvatarUrl.contains(",")) {
            try {
                val pureBase64 = userAvatarUrl.substringAfter(",")
                val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    DisposableEffect(userId) {
        val userListener = if (userId.isNotEmpty()) {
            db.collection("users").document(userId).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val nameFromDb = snapshot.getString("name")
                    userName = if (!nameFromDb.isNullOrEmpty()) nameFromDb else userEmail.substringBefore("@")
                    userAvatarUrl = snapshot.getString("avatarUri") ?: ""
                } else {
                    userName = userEmail.substringBefore("@")
                }
            }
        } else null

        // Подгружаем врачей для ленты главного экрана
        val doctorsListener = db.collection("users")
            .whereEqualTo("role", "doctor")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    dbDoctors = snapshot.toObjects(User::class.java)
                }
            }

        onDispose {
            userListener?.remove()
            doctorsListener?.remove()
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

            dayList.add(CalendarDay(day = dayNum.toString(), name = dayName, isSelected = dayNum == currentDayOfMonth))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        dayList
    }

    val todayText = remember {
        val calendar = Calendar.getInstance()
        val dayNum = calendar.get(Calendar.DAY_OF_MONTH)
        val dayNameFormat = SimpleDateFormat("EEEE", Locale.US)
        val formattedName = dayNameFormat.format(calendar.time)
        "$dayNum $formattedName - Today"
    }

    val filteredDoctors = dbDoctors.filter { doc ->
        doc.name.contains(homeSearchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
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
                        if (avatarBitmap != null) {
                            Image(
                                bitmap = avatarBitmap.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(48.dp).clip(CircleShape)
                            )
                        } else {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MedPrimary.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = MedPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(text = "Добро пожаловать", fontSize = 13.sp, color = TextSecondary)
                            Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                    Row {
                        Icon(Icons.Default.Notifications, null, tint = SplashBackground, modifier = Modifier.size(26.dp).clickable { onNotificationClick() })
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.Settings, null, tint = SplashBackground, modifier = Modifier.size(26.dp).clickable { onSettingsClick() })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onDoctorsTabClick() }) {
                        Icon(Icons.Default.Search, null, tint = SplashBackground, modifier = Modifier.size(24.dp))
                        Text(text = "Doctors", fontSize = 12.sp, color = SplashBackground, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    TextField(
                        value = homeSearchQuery,
                        onValueChange = { homeSearchQuery = it },
                        placeholder = { Text("Search...", color = TextSecondary, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Tune, null, tint = TextSecondary) },
                        trailingIcon = { Icon(Icons.Default.Search, null, tint = SplashBackground) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        colors = TextFieldDefaults.colors(focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface, disabledContainerColor = MedSurface, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    items(items = days) { dayItem: CalendarDay ->
                        Box(
                            modifier = Modifier.size(width = 54.dp, height = 76.dp).clip(RoundedCornerShape(27.dp)).background(if (dayItem.isSelected) SplashBackground else MedSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = dayItem.day, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (dayItem.isSelected) MedSurface else TextPrimary)
                                Text(text = dayItem.name, fontSize = 11.sp, color = if (dayItem.isSelected) MedSurface.copy(alpha = 0.7f) else TextSecondary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = todayText, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SplashBackground)
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDoctors) { doc ->
                    val docBitmap = if (doc.avatarUri.isNotEmpty() && doc.avatarUri.contains(",")) {
                        try {
                            val pureBytes = doc.avatarUri.substringAfter(",")
                            val bytes = Base64.decode(pureBytes, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        } catch(e: Exception) { null }
                    } else null
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onDoctorClick(doc.uid) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E4FF).copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (docBitmap != null) {
                                Image(bitmap = docBitmap.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(60.dp).clip(CircleShape))
                            } else {
                                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, tint = Color.DarkGray)
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(doc.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SplashBackground)
                                Text("Онлайн-консультант", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("5.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}