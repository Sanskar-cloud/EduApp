package com.example.data.repository.resource

import com.example.data.models.Resource
import com.example.data.requests.ResourceRequest
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext


import java.net.URL
import java.net.URLConnection
import kotlin.math.roundToInt

class ResourceRepositoryImpl(
    db: MongoDatabase
): ResourceRepository {

    private val resources = db.getCollection<Resource>("Resource")
    override suspend fun getResourcesForCourse(courseId: String, app: Application): List<Resource>? {
        app.log.info(courseId)
        val resources = resources.find(Filters.eq("courseId",courseId)).toList()
        app.log.info(resources.toString())
        return resources
    }

    override suspend fun createResource(resource: ResourceRequest, app: Application): Boolean {
        return resources.insertOne(
            Resource(
                courseId = resource.courseId,
                resourceName = urlToName(resource.resourceUrl, app),
                resourceUrl = resource.resourceUrl,
                resourceSize = measureFileSize(resource.resourceUrl, app),
                fileType = urlToFileType(resource.resourceUrl)
            )
        ).wasAcknowledged()
    }

    override suspend fun updateResource(resourceId: String, resourceUrl: String?, app: Application): Boolean {
        return resources.updateOne(
            Filters.eq("resourceId",resourceId),


            Updates.combine(
                Updates.set("resourceUrl",resourceUrl),
                Updates.set("resourceName",resourceUrl?.let { urlToName(it, app) }),
                Updates.set("resourceSize",resourceUrl?.let { measureFileSize(it, app) }),
                Updates.set("fileType",resourceUrl?.let { urlToFileType(it) }),

            )

        ).wasAcknowledged()
    }

    override suspend fun deleteResource(resourceId: String): Boolean {
        return resources.deleteOne(
            Filters.eq("resourceId",resourceId)
        ).wasAcknowledged()
    }

    override suspend fun deleteAllResources(courseId: String): Boolean {
        return resources.deleteMany(Filters.eq("courseId",courseId)).wasAcknowledged()
    }

}

private fun urlToName(url: String, app: Application): String {
    val fileName: String = url.substring(url.lastIndexOf('/') + 1, url.length)
    app.log.info(fileName)
    return fileName.substring(0, fileName.lastIndexOf('.'))
}

private fun urlToFileType(url: String): String {
    return url.substring(url.lastIndexOf('.') + 1, url.length)
}

private suspend fun measureFileSize(fileUrl: String, app: Application): Double {
    val url = URL(fileUrl)
    val urlConnection: URLConnection = withContext(Dispatchers.IO) {
        url.openConnection()
    }
    withContext(Dispatchers.IO) {
        urlConnection.connect()
    }
    val file_size = urlConnection.contentLength.toDouble()
    val sizeInMb = (file_size/1048576)
    val roundoff = (sizeInMb * 100.0).roundToInt() / 100.0
    app.log.info(roundoff.toString())
    return roundoff
}