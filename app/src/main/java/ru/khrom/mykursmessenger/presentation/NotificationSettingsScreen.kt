package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun NotificationSettingsScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    var generalNotification by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }
    var soundCall by remember { mutableStateOf(false) }
    var vibrate by remember { mutableStateOf(true) }
    var specialOffers by remember { mutableStateOf(false) }
    var payments by remember { mutableStateOf(true) }
    var promoAndDiscount by remember { mutableStateOf(false) }
    var cashback by remember { mutableStateOf(true) }

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
                    text = "Notification Setting",
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
                Spacer(modifier = Modifier.height(24.dp))

                SwitchSettingItem("General Notification", generalNotification) { generalNotification = it }
                SwitchSettingItem("Sound", sound) { sound = it }
                SwitchSettingItem("Sound Call", soundCall) { soundCall = it }
                SwitchSettingItem("Vibrate", vibrate) { vibrate = it }
                SwitchSettingItem("Special Offers", specialOffers) { specialOffers = it }
                SwitchSettingItem("Payments", payments) { payments = it }
                SwitchSettingItem("Promo and Discount", promoAndDiscount) { promoAndDiscount = it }
                SwitchSettingItem("Cashback", cashback) { cashback = it }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SwitchSettingItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            fontFamily = FontFamily.Default
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MedSurface,
                checkedTrackColor = SplashBackground,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color(-0xFFD6E4FF).copy(alpha = 0.5f)
            )
        )
    }
}
