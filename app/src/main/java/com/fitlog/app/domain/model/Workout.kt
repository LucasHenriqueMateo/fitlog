package com.fitlog.app.domain.model

data class Workout(
    val id: String = "",
    val userId: String = "",
    val name: String,
    val date: String,
    val notes: String = "",
    val exercises: List<Exercise> = emptyList(),
    val createdAt: String = ""
)
