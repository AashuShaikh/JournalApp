package com.aashushaikh.journalapp.model

import com.google.firebase.Timestamp

data class Journal(
    val title: String = "",
    val thoughts: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val username: String = "",
    val timestamp: Timestamp = Timestamp.now() // Or a suitable default Timestamp
)
