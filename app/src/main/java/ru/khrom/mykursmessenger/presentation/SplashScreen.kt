package ru.khrom.mykursmessenger.presentation

import android.Manifest
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.MedSurface
import ru.khrom.mykursmessenger.ui.theme.SplashBackground

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    val context = LocalContext.current

    // Надежный лончер множественных разрешений для живых телефонов
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // После закрытия системного диалога запускаем переход
        onNavigateNext()
    }

    LaunchedEffect(Unit) {
        // Принудительно вызываем системное окно Android на камеру и микрофон
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Text("Skin First", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MedSurface)
    }
}
