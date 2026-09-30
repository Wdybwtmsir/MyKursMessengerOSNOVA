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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val userEmail = auth.currentUser?.email ?: "example@mail.com"

    var userName by remember { mutableStateOf("Загрузка...") }
    var userPhone by remember { mutableStateOf("Не указан") }
    var userBirthDate by remember { mutableStateOf("Не указана") }
    var userGender by remember { mutableStateOf("Не указан") }
    var userAvatarUrl by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    userName = snapshot.getString("name") ?: userEmail.substringBefore("@")
                    userPhone = snapshot.getString("phone") ?: "Не указан"
                    userBirthDate = snapshot.getString("birthDate") ?: "Не указана"
                    userGender = snapshot.getString("gender") ?: "Не указан"
                    userAvatarUrl = snapshot.getString("avatarUri") ?: ""
                } else {
                    userName = userEmail.substringBefore("@")
                }
            }
        }
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
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
                    modifier = Modifier.padding(8.dp).clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userAvatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = userAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(96.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(96.dp).clip(CircleShape).background(MedPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = userName.take(1).uppercase(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MedPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                Text(text = userEmail, fontSize = 14.sp, color = TextSecondary, fontFamily = FontFamily.Default)
            }

            // Блок вывода личной информации пользователя напрямую из Firestore
            Text(text = "Personal Info", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, null, tint = SplashBackground, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Phone Number", fontSize = 12.sp, color = TextSecondary)
                            Text(userPhone, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cake, null, tint = SplashBackground, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Birth Date", fontSize = 12.sp, color = TextSecondary)
                            Text(userBirthDate, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Male, null, tint = SplashBackground, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Gender", fontSize = 12.sp, color = TextSecondary)
                            Text(userGender, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        }
                    }
                }
            }

            Text(text = "Account Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedSurface)
            ) {
                Column {
                    ProfileMenuItem(icon = Icons.Default.Edit, title = "Edit Profile", onClick = onEditProfileClick)
                    HorizontalDivider(color = MedBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(icon = Icons.Default.Settings, title = "Settings", onClick = onBackClick)
                    HorizontalDivider(color = MedBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Log Out",
                        titleColor = Color(0xFFEF4444),
                        iconTint = Color(0xFFEF4444),
                        onClick = {
                            auth.signOut()
                            onLogoutSuccess()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, titleColor: Color = TextPrimary, iconTint: Color = SplashBackground, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = titleColor, fontFamily = FontFamily.Default)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary.copy(alpha = 0.4f))
    }
}