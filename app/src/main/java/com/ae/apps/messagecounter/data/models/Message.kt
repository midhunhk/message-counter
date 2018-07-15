package com.ae.apps.messagecounter.data.models

data class Message(val id: String,
                   val messageCount: Int,
                   val body: String,
                   val date: String,
                   val protocol: String?,
                   val address: String)