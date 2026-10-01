package ru.khrom.mykursmessenger.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun EditProfileScreen(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var birthDateInput by remember { mutableStateOf("") }
    var genderInput by remember { mutableStateOf("") }
    var base64Avatar by remember { mutableStateOf("") }
    var isPending by remember { mutableStateOf(false) }

    val avatarBitmap = remember(base64Avatar) {
        if (base64Avatar.isNotEmpty() && base64Avatar.contains(",")) {
            try {
                val pureBase64 = base64Avatar.substringAfter(",")
                val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(selectedUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)

                if (originalBitmap != null) {
                    val targetWidth = 250
                    val targetHeight = (originalBitmap.height * (250.0 / originalBitmap.width)).toInt()
                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

                    val outputStream = ByteArrayOutputStream()
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                    val byteArray = outputStream.toByteArray()

                    base64Avatar = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Не удалось обработать фото", Toast.LENGTH_SHORT).show()
            }
        }
    }

    DisposableEffect(userId) {
        val listener = if (userId.isNotEmpty()) {
            db.collection("users").document(userId).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    nameInput = snapshot.getString("name") ?: ""
                    phoneInput = snapshot.getString("phone") ?: ""
                    birthDateInput = snapshot.getString("birthDate") ?: ""
                    genderInput = snapshot.getString("gender") ?: ""
                    base64Avatar = snapshot.getString("avatarUri") ?: ""
                }
            }
        } else null

        onDispose {
            listener?.remove()
        }
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
                        .clickable(enabled = !isPending) { onBackClick() }
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
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(SplashBackground.copy(alpha = 0.1f))
                        .clickable(enabled = !isPending) { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarBitmap != null) {
                        Image(
                            bitmap = avatarBitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SplashBackground,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Нажмите, чтобы изменить фото",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SplashBackground,
                    modifier = Modifier.clickable(enabled = !isPending) { galleryLauncher.launch("image/*") }
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Исправлено: жестко прописали цвета focusedTextColor и unfocusedTextColor во все поля
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("ФИО", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, color = TextPrimary),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = SplashBackground,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Номер телефона", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, color = TextPrimary),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = SplashBackground,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )
                OutlinedTextField(
                    value = birthDateInput,
                    onValueChange = { birthDateInput = it },
                    label = { Text("Дата рождения (ДД/ММ/ГГГГ)", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, color = TextPrimary),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = SplashBackground,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )
                OutlinedTextField(
                    value = genderInput,
                    onValueChange = { genderInput = it },
                    label = { Text("Пол", fontFamily = FontFamily.Default) },
                    textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, color = TextPrimary),
                    enabled = !isPending,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = SplashBackground,
                        unfocusedLabelColor = TextSecondary,
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPending) {
                        CircularProgressIndicator(color = SplashBackground)
                    } else {
                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    isPending = true
                                    val updates = mapOf(
                                        "name" to nameInput.trim(),
                                        "phone" to phoneInput.trim(),
                                        "birthDate" to birthDateInput.trim(),
                                        "gender" to genderInput.trim(),
                                        "avatarUri" to base64Avatar.trim()
                                    )
                                    db.collection("users").document(userId).set(updates, com.google.firebase.firestore.SetOptions.merge())
                                        .addOnSuccessListener {
                                            isPending = false
                                            onSaveClick()
                                        }
                                        .addOnFailureListener {
                                            isPending = false
                                            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SplashBackground),
                            shape = RoundedCornerShape(16.dp),
                            enabled = nameInput.isNotBlank()
                        ) {
                            Text("Сохранить изменения", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                        }
                    }
                }
            }
        }
    }
}