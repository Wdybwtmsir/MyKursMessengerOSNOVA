package ru.khrom.mykursmessenger.data

import ru.khrom.mykursmessenger.R

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val lastMessage: String = "",
    val time: String = "",
    val isOnline: Boolean = false,
    val avatarUrl: String = "",
    val gender: String = "",
    val isFavorite: Boolean = false
)
fun Doctor.getLocalAvatarRes(): Int {
    return when (this.avatarUrl) {
        "doc_alex" -> R.drawable.doc_alex
        "doc_maria" -> R.drawable.doc_maria
        "doc_sergey" -> R.drawable.doc_sergey
        "doc_elena" -> R.drawable.doc_elena
        else -> R.drawable.doc_alex
    }
}
