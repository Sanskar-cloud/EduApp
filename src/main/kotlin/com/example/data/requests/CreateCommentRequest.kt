package com.example.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val comment: String,
    val courseId: String,
)