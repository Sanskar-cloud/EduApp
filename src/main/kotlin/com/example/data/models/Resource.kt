package com.example.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Resource(
    @SerialName("_id")
    val resourceId: String = ObjectId().toString(),
    val courseId: String,
    val resourceUrl: String,
    val resourceName: String,
    val resourceSize: Double,
    val fileType: String
)
