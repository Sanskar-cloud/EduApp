package com.example.service

import io.ktor.server.application.*
import com.example.data.models.Resource
import com.example.data.repository.resource.ResourceRepository
import com.example.data.requests.ResourceRequest

class ResourceService(
    private val resourceRepository: ResourceRepository
) {

    suspend fun getResourcesForCourse(courseId: String, app: Application): List<Resource>? {
        return resourceRepository.getResourcesForCourse(courseId, app)
    }

    suspend fun createResource(
        courseId: String,
        resourceUrl: String,
        app: Application
    ): Boolean {
        return resourceRepository.createResource(
            ResourceRequest(courseId = courseId, resourceUrl = resourceUrl),
            app = app
        )
    }

    suspend fun updateResource(
        resourceId: String,
        resourceUrl: String?,
        app: Application
    ): Boolean {
        return resourceRepository.updateResource(
            resourceId,
            resourceUrl,
            app
        )
    }
    suspend fun deleteResource(resourceId: String): Boolean {
        return resourceRepository.deleteResource(resourceId)
    }

    suspend fun deleteAllResource(courseId: String): Boolean {
        return resourceRepository.deleteAllResources(courseId)
    }
}