package ru.khrom.mykursmessenger.presentation

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*
import java.io.File
import java.io.FileOutputStream

fun saveImageToInternalStorage(context: Context, uri: Uri, userId: String): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "avatar_$userId.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.toUri()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun EditProfileScreen(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var medicalNotes by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var avatarUrlString by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedUri = saveImageToInternalStorage(context, uri, userId)
            if (savedUri != null) {
                imageUri = savedUri
                avatarUrlString = savedUri.toString()
                Toast.makeText(context, "Фото выбрано!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        name = document.getString("name") ?: ""
                        phone = document.getString("phone") ?: ""
                        dob = document.getString("dob") ?: ""
                        gender = document.getString("gender") ?: ""
                        medicalNotes = document.getString("medicalNotes") ?: ""
                        val uriStr = document.getString("avatarUri") ?: ""
                        avatarUrlString = uriStr
                        if (uriStr.isNotEmpty()) {
                            imageUri = uriStr.toUri()
                        }
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MedPrimary)
            }
        } else {
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
                        text = "Редактировать профиль",
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

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MedPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (name.isNotEmpty()) name.take(1) else "П",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedPrimary,
                                    fontFamily = FontFamily.Default
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MedPrimary)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MedSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Полное имя", fontFamily = FontFamily.Default) },
                        placeholder = { Text("Введите имя", color = TextSecondary) },
                        textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = MedPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = MedPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                            focusedContainerColor = MedSurface,
                            unfocusedContainerColor = MedSurface
                        )
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Номер телефона", fontFamily = FontFamily.Default) },
                        placeholder = { Text("+7 (999) 000-00-00", color = TextSecondary) },
                        textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = MedPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = MedPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                            focusedContainerColor = MedSurface,
                            unfocusedContainerColor = MedSurface
                        )
                    )
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Дата рождения", fontFamily = FontFamily.Default) },
                        placeholder = { Text("ДД/ММ/ГГГГ", color = TextSecondary) },
                        textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = TextSecondary) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = MedPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = MedPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                            focusedContainerColor = MedSurface,
                            unfocusedContainerColor = MedSurface
                        )
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Пол", fontFamily = FontFamily.Default) },
                        placeholder = { Text("Мужской / Женский", color = TextSecondary) },
                        textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = MedPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = MedPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                            focusedContainerColor = MedSurface,
                            unfocusedContainerColor = MedSurface
                        )
                    )
                    OutlinedTextField(
                        value = medicalNotes,
                        onValueChange = { medicalNotes = it },
                        label = { Text("Медицинские примечания", fontFamily = FontFamily.Default) },
                        placeholder = { Text("Аллергии, хронические заболевания...", color = TextSecondary) },
                        textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = MedPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedBorderColor = MedPrimary,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                val userMap = hashMapOf(
                                    "name" to name,
                                    "phone" to phone,
                                    "dob" to dob,
                                    "gender" to gender,
                                    "medicalNotes" to medicalNotes,
                                    "avatarUri" to avatarUrlString
                                )
                                db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener { onSaveClick() }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Сохранить изменения",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MedSurface,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
            }
        }
    }
}
