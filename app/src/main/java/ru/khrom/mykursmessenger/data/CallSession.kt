package ru.khrom.mykursmessenger.data

data class CallSession(
    val callId: String = "",
    val callerId: String = "",
    val callerName: String = "",
    val receiverId: String = "",
    val type: String = "voice", //
    val status: String = "ringing" //
)
