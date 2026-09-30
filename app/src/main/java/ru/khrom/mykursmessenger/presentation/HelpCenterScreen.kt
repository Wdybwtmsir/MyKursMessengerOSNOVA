package ru.khrom.mykursmessenger.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
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

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String
)

@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("FAQ", "Contact Us")

    val faqList = remember {
        listOf(
            FaqItem(1, "How do I book an appointment?", "You can book an appointment by selecting your preferred doctor on the Home screen or Doctors list, clicking 'Book Appointment', choosing an available date and time slot, and completing the payment process."),
            FaqItem(2, "Can I cancel my appointment?", "Yes, you can cancel your appointment from the 'All Appointment' screen under the 'Upcoming' tab. Please note that cancellations should be made at least 24 hours before the scheduled time."),
            FaqItem(3, "How to change password?", "Go to your Profile screen, click on 'Settings', select 'Password Manager', enter your current password, and then provide a new secure password."),
            FaqItem(4, "Is my medical data secure?", "Absolutely. Skin First uses enterprise-grade encryption to protect all your personal information, medical history, chat messages, and consultation notes."),
            FaqItem(5, "How do video calls work?", "Once your appointment time arrives, open the chat with your doctor and click the video camera icon in the top right corner to start a secure video consultation.")
        )
    }

    var expandedItemId by remember { mutableStateOf<Int?>(null) }

    val filteredFaq = faqList.filter {
        it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true)
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
                        .clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Help Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Default
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC6D6FF).copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("How Can We Help You?", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SplashBackground) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = MedPrimary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.2f),
                        focusedContainerColor = MedSurface,
                        unfocusedContainerColor = MedSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Button(
                            onClick = { selectedTab = index },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) SplashBackground else Color(0xFFD6E4FF).copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(19.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MedSurface else SplashBackground,
                                fontFamily = FontFamily.Default
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredFaq) { item ->
                        val isExpanded = expandedItemId == item.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFD6E4FF).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MedSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedItemId = if (isExpanded) null else item.id }
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.question,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f),
                                        fontFamily = FontFamily.Default
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = SplashBackground
                                    )
                                }
                                AnimatedVisibility(visible = isExpanded) {
                                    Column {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = item.answer,
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            lineHeight = 20.sp,
                                            fontFamily = FontFamily.Default
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Contact channels from Figma Slide 6",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Default
                    )
                }
            }
        }
    }
}
