package com.example.lib.data.notes

data class CreateNoteRequest(
    val content: String,
    val time: String,
    val hiveId: Int
)
