package ru.khrom.mykursmessenger.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNotificationSettingClick: () -> Unit,
    onPasswordManagerClick: () -> Unit,
    onHelpCenterClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onAccountDeletedSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Account?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Default
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete your account? This will erase all your medical records, profile data, and chat history. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Default
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (currentUser != null) {
                            isDeleting = true
                            val userId = currentUser.uid

                            // 1. Сначала удаляем личные документы из Firestore
                            db.collection("users").document(userId).delete()
                                .addOnSuccessListener {
                                    // 2. Затем удаляем учетную запись из Firebase Auth
                                    currentUser.delete()
                                        .addOnSuccessListener {
                                            isDeleting = false
                                            showDeleteDialog = false
                                            Toast.makeText(context, "Account permanently deleted.", Toast.LENGTH_SHORT).show()
                                            onAccountDeletedSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            isDeleting = false
                                            showDeleteDialog = false
                                            Toast.makeText(context, "Error: Re-authentication required to delete account.", Toast.LENGTH_LONG).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isDeleting = false
                                    Toast.makeText(context, "Failed to erase database entry.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isDeleting
                ) {
                    Text("Cancel", color = SplashBackground)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MedSurface
        )
    }

    Scaffold(
        containerColor = MedBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                            .clickable(enabled = !isDeleting) { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Default
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedSurface)
                ) {
                    Column {
                        SettingsMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notification Setting",
                            onClick = { if (!isDeleting) onNotificationSettingClick() }
                        )
                        HorizontalDivider(color = MedBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Default.Lock,
                            title = "Password Manager",
                            onClick = { if (!isDeleting) onPasswordManagerClick() }
                        )
                        HorizontalDivider(color = MedBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            onClick = { if (!isDeleting) onPrivacyPolicyClick() }
                        )
                        HorizontalDivider(color = MedBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Default.HelpCenter,
                            title = "Help Center",
                            onClick = { if (!isDeleting) onHelpCenterClick() }
                        )
                        HorizontalDivider(color = MedBackground, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsMenuItem(
                            icon = Icons.Default.Delete,
                            title = "Delete Account",
                            titleColor = Color(0xFFEF4444),
                            iconTint = Color(0xFFEF4444),
                            onClick = { if (!isDeleting) showDeleteDialog = true }
                        )
                    }
                }
            }

            if (isDeleting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SplashBackground)
                }
            }
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = TextPrimary,
    iconTint: Color = SplashBackground,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = titleColor, fontFamily = FontFamily.Default)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary.copy(alpha = 0.4f))
    }
}