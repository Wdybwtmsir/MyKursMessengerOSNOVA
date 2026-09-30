package ru.khrom.mykursmessenger.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.khrom.mykursmessenger.data.Appointment
import ru.khrom.mykursmessenger.data.DoctorRepository
import ru.khrom.mykursmessenger.ui.theme.*
import java.util.UUID

@Composable
fun PaymentScreen(
    doctorId: String,
    dateText: String,
    timeSlot: String,
    bookingFor: String,
    problemDescription: String,
    onBackClick: () -> Unit,
    onPaymentComplete: () -> Unit
) {
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var step by remember { mutableIntStateOf(1) }
    var selectedMethod by remember { mutableStateOf("Credit Card") }

    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val doctor = remember(doctorId) {
        DoctorRepository.doctors.find { it.id == doctorId } ?: DoctorRepository.doctors.first()
    }

    fun sendAppointmentToFirestore() {
        val uniqueId = UUID.randomUUID().toString()

        val newAppointment = Appointment(
            id = uniqueId,
            userId = currentUserId,
            doctorId = doctorId,
            doctorName = doctor.name,
            doctorSpecialty = doctor.specialty,
            doctorAvatarUrl = doctor.avatarUrl,
            dateText = dateText,
            timeSlot = timeSlot,
            patientName = if (bookingFor == "Yourself") "User Profile Name" else "Jane Doe",
            patientAge = if (bookingFor == "Yourself") "—" else "30",
            patientGender = if (bookingFor == "Yourself") "—" else "Female",
            problemDescription = problemDescription,
            status = "Upcoming"
        )

        db.collection("appointments").document(uniqueId).set(newAppointment)
            .addOnSuccessListener {
                step = 3
            }
    }

    if (step == 3) {
        Box(
            modifier = Modifier.fillMaxSize().background(SplashBackground).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(MedSurface), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SplashBackground, modifier = Modifier.size(56.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Congratulation", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MedSurface, fontFamily = FontFamily.Default)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Payment is Successfully", fontSize = 16.sp, color = MedSurface.copy(alpha = 0.8f), fontFamily = FontFamily.Default, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = { onPaymentComplete() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Go to Chats", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SplashBackground, fontFamily = FontFamily.Default)
                }
            }
        }
    } else {
        Scaffold(
            containerColor = MedBackground
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(MedSurface).padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextPrimary, modifier = Modifier.padding(8.dp).clickable { if (step == 2) step = 1 else onBackClick() })
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = if (step == 1) "Payment Method" else "Add Card", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                }

                Column(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(scrollState).padding(horizontal = 24.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))

                    if (step == 1) {
                        Text("Credit & Debit Card", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().height(56.dp).background(MedSurface, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFD6E4FF), RoundedCornerShape(12.dp)).clickable { selectedMethod = "Credit Card" }.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Add New Card", fontSize = 15.sp, color = TextPrimary, fontFamily = FontFamily.Default)
                            RadioButton(selected = selectedMethod == "Credit Card", onClick = { selectedMethod = "Credit Card" })
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("More Payment Option", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Default)
                        Spacer(modifier = Modifier.height(12.dp))
                        listOf("Apple Pay", "PayPal", "Google Play").forEach { method ->
                            Row(
                                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 12.dp).background(MedSurface, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFD6E4FF), RoundedCornerShape(12.dp)).clickable { selectedMethod = method }.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(method, fontSize = 15.sp, color = TextPrimary, fontFamily = FontFamily.Default)
                                RadioButton(selected = selectedMethod == method, onClick = { selectedMethod = method })
                            }
                        }
                    } else {
                        Card(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SplashBackground)) {
                            Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Text("000 000 000 00", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Card Holder Name", fontSize = 11.sp, color = MedSurface.copy(alpha = 0.6f))
                                        Text(if (cardHolder.isEmpty()) "John Doe" else cardHolder, fontSize = 14.sp, color = MedSurface, fontWeight = FontWeight.Medium)
                                    }
                                    Column {
                                        Text("Expiry Date", fontSize = 11.sp, color = MedSurface.copy(alpha = 0.6f))
                                        Text(if (expiryDate.isEmpty()) "04/28" else expiryDate, fontSize = 14.sp, color = MedSurface, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedTextField(value = cardHolder, onValueChange = { cardHolder = it }, label = { Text("Card Holder Name", fontFamily = FontFamily.Default) }, textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface))
                        OutlinedTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = { Text("Card Number", fontFamily = FontFamily.Default) }, textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = { Text("Expiry Date", fontFamily = FontFamily.Default) }, textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface))
                            OutlinedTextField(value = cvv, onValueChange = { cvv = it }, label = { Text("CVV", fontFamily = FontFamily.Default) }, textStyle = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = MedSurface, unfocusedContainerColor = MedSurface))
                        }
                    }
                }
                Surface(tonalElevation = 8.dp, color = MedSurface, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Button(
                            onClick = {
                                if (step == 1) {
                                    if (selectedMethod == "Credit Card") step = 2 else sendAppointmentToFirestore()
                                } else {
                                    if (cardHolder.isNotEmpty() && cardNumber.isNotEmpty()) sendAppointmentToFirestore()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = SplashBackground), shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = if (step == 1) "Continue" else "Save Card", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedSurface)
                        }
                    }
                }
            }
        }
    }
}