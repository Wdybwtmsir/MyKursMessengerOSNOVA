package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.khrom.mykursmessenger.ui.theme.MedPrimary
import ru.khrom.mykursmessenger.ui.theme.MedSurface

@Composable
fun SplashScreen(onNavigateNext: (String) -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateNext("welcome")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MedPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Medical Chat",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MedSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dermatology & Health Community",
                fontSize = 14.sp,
                color = MedSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = MedSurface,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
