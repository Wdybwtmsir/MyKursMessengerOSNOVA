
package ru.khrom.mykursmessenger.data

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val avatarUri: String = "",
    val role: String = "patient", // "patient" или "doctor"
    val activeCallId: String = ""  // Сюда пишется ID звонка, если идет вызов
)
