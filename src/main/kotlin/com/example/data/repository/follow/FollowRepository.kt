package com.example.data.repository.follow

import com.example.data.models.Following
import io.ktor.server.application.*


interface FollowRepository {
    suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String,
        app: Application
    ): Boolean

    suspend fun unfollowUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun getFollowsByUser(userId: String): List<Following>

    suspend fun doesUserFollow(followingUserId: String, followedUserId: String): Boolean
}