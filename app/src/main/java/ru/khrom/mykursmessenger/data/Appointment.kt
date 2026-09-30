package ru.khrom.mykursmessenger.data

data class Appointment(
    val id: String = "",
    val userId: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val doctorSpecialty: String = "",
    val doctorAvatarUrl: String = "",
    val dateText: String = "",
    val timeSlot: String = "",
    val patientName: String = "",
    val patientAge: String = "",
    val patientGender: String = "",
    val problemDescription: String = "",
    val status: String = "Upcoming" // Upcoming, Complete, Cancelled
)
