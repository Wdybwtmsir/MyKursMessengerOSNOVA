package ru.khrom.mykursmessenger.data

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val lastMessage: String = "",
    val time: String = "",
    val isOnline: Boolean = false,
    val avatarUrl: String = "", // Строковая URL ссылка вместо R.drawable
    val gender: String = "",
    val isFavorite: Boolean = false
)
