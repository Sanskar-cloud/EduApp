package com.example.data.requests

import com.example.util.DurationSerializer
import kotlinx.serialization.Serializable

import kotlin.time.Duration

@Serializable
data class CourseRequest(
    val courseTitle: String,
    val description: String,
    val tag: String? = null,
    @Serializable(with = DurationSerializer::class)
    val duration: Duration
)

