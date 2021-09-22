package me.geso.blog3.entity

import java.time.LocalDateTime

data class Entry(
    val path: String,
    val title: String,
    val body: String,
    val status: String,
    val format: String, // TODO make this enum
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
