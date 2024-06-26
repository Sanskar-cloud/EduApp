package com.example.service

import io.ktor.server.application.*
import com.example.data.models.Lesson
import com.example.data.repository.lesson.LessonRepository
import com.example.data.requests.LessonRequest
import com.example.util.Constants

class LessonService(
    private val lessonRepository: LessonRepository
) {

    suspend fun getLesson(lessonId: String): Lesson? {
        return lessonRepository.getLesson(lessonId)
    }

    suspend fun getLessonsForCourse(courseId: String): List<Lesson>? {
        return lessonRepository.getLessonsForCourse(courseId)
    }
    suspend fun createLesson(courseId: String, lessonThumbnailUrl: String?, videoUrl: String?, createLessonRequest: LessonRequest, app: Application): Boolean {
        return lessonRepository.createLesson(
            Lesson(
                courseId = courseId,
                lessonNo = createLessonRequest.lessonNo,
                name = createLessonRequest.name,
                thumbnail = lessonThumbnailUrl?: Constants.PROFILE_PICTURE_PATH,
                desc = createLessonRequest.desc,
                lessonVideoUrl = videoUrl?: Constants.PROFILE_PICTURE_PATH
            ),
            app = app
        )
    }

    suspend fun deleteLesson(lessonId: String): Boolean {
        return lessonRepository.deleteLesson(lessonId)
    }

    suspend fun deleteAllLesson(courseId: String): Boolean {
        return lessonRepository.deleteLessonsFromCourse(courseId)
    }

    suspend fun updateLessonForCourse(
        lessonId: String,
        lesson: LessonRequest,
        videoUrl: String?,
        lessonThumbnailUrl: String?,
        app: Application
    ): Boolean {
        return lessonRepository.updateLesson(lessonId, lessonThumbnailUrl, videoUrl, lesson, app)
    }
}