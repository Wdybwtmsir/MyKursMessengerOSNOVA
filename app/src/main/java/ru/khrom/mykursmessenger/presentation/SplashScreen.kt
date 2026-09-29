package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.khrom.mykursmessenger.R
import ru.khrom.mykursmessenger.ui.theme.MedSurface
import ru.khrom.mykursmessenger.ui.theme.SplashBackground
import ru.khrom.mykursmessenger.ui.theme.TextPrimary
import ru.khrom.mykursmessenger.ui.theme.TextSecondary

@Composable
fun SplashScreen(onNavigateNext: (String) -> Unit) {

    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateNext("welcome")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splashlogo),
                contentDescription = null,
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Skin First",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MedSurface,
                fontFamily = FontFamily.Default
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Medical Chat",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MedSurface,
                fontFamily = FontFamily.Default
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Dermatology & Health Community",
                fontSize = 14.sp,
                color = MedSurface.copy(alpha = 0.8f),
                fontFamily = FontFamily.Default
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
