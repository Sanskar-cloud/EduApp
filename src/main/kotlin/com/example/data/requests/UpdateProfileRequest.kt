package com.example.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val username: String,
    val bio: String,
    val faceBookUrl: String,
    val instagramUrl: String,
    val twitterUrl: String,
)
