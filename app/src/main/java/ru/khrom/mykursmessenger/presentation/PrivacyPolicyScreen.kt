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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.khrom.mykursmessenger.ui.theme.*

@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

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
                    text = "Privacy Policy",
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

                Text(
                    text = "1. Types of Data We Collect",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Skin First collects personal identification information (Name, email address, phone number, date of birth) and healthcare-related medical data, including uploaded skin photos, diagnosis logs, consultation history, and chat messages with dermatologists. This information is required to provide accurate medical assessment and remote consultation services.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "2. How We Protect Your Medical History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your privacy is our highest priority. All medical interactions, audio/video call stream segments, and server transactions are fully encrypted using end-to-end industry standards (AES-256 protocols). We strictly comply with healthcare information security acts to ensure that unauthorized third parties cannot access or intercept your personal health files.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "3. Third-Party Data Disclosures",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Skin First does not sell, trade, or distribute your private consultations or biometric logs to any advertising agencies or marketing partners. Data is only accessible to the certified doctors you explicitly choose to book appointments with inside this platform.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "4. User Accounts and Data Control",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You have full control over your diagnostic history. At any moment, you can request an absolute deletion of your cloud-saved profile directly through the 'Delete Account' utility within the settings console. Once processed, all database documents associated with your unique ID will be wiped permanently from our systems.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily.Default
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
