package com.example.data.repository.course

import UserTchr
import com.example.data.models.*
import com.example.data.requests.CourseRequest
import com.mongodb.client.model.Accumulators.avg
import com.mongodb.client.model.Aggregates.group
import com.mongodb.client.model.Aggregates.match
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.*
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document

import java.util.*

class CourseRepositoryImpl(db: MongoDatabase): CourseRepository {

    private val courses = db.getCollection<Course>("Course")
    private val users = db.getCollection<User>("User")
    private val enrollments = db.getCollection<Enrollment>("Enrollment")
    private val ratings = db.getCollection<Rating>("Rating")
    private val categories = db.getCollection<Category>("Category")
    private val bookmarks = db.getCollection<Bookmark>("Bookmark")
    override suspend fun searchCourses(
        query: String,
        page: Int,
        pageSize: Int,
        app: Application
    ): List<CourseOverview>? {
        TODO("Not yet implemented")
    }


    override suspend fun getCourseById(id: String): Course? {
        return courses.find(Filters.eq("courseId", id)).firstOrNull()

    }

    override suspend fun getCoursesForProfile(userId: String, page: Int, pageSize: Int, app: Application): List<CourseOverview>? {
        val courseOverviews =  courses.find(Filters.eq("userId", userId))
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
            .map { course ->
                app.log.info(course.courseTitle)
                CourseOverview(
                    courseId = course.courseId,
                    courseName = course.courseTitle,
                    courseTeacherName= course.courseTeacher!!.username,
                    courseThumbnailUrl = course.courseThumbnail,
                    rating = course.avgRating,
                    noOfStudentRated = course.noOfStudentRated,
                    noOfStudentEnrolled = course.noOfStudentEnrolled,
                    tag = course.tag
                )

            }
        return courseOverviews
    }

    override suspend fun getMostWatchedCourses(page: Int, pageSize: Int, app: Application): List<CourseOverview> {

        val courseOverviews =  courses.find()
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
            .map { course ->
                app.log.info(course.courseTitle)
                CourseOverview(
                    courseId = course.courseId,
                    courseName = course.courseTitle,
                    courseTeacherName= course.courseTeacher!!.username,
                    courseThumbnailUrl = course.courseThumbnail,
                    rating = course.avgRating,
                    noOfStudentRated = course.noOfStudentRated,
                    noOfStudentEnrolled = course.noOfStudentEnrolled,
                    tag = course.tag
                )

            }

        return courseOverviews

    }

    override suspend fun getPreviousWatchedCourses(page: Int, pageSize: Int): List<CourseOverview>? {
        return courses.find()
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
            .map {course ->
                CourseOverview(
                    courseId = course.courseId,
                    courseName = course.courseTitle,
                    courseTeacherName= course.courseTeacher!!.username,
                    courseThumbnailUrl = course.courseThumbnail,
                    rating = course.avgRating,
                    noOfStudentRated = course.noOfStudentRated,
                    noOfStudentEnrolled = course.noOfStudentEnrolled,
                    tag = course.tag
                )
            }
    }

    override suspend fun getOthersWatchedCourses(page: Int, pageSize: Int): List<CourseOverview>? {
        return courses.find()
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
            .map {course ->
                CourseOverview(
                    courseId = course.courseId,
                    courseName = course.courseTitle,
                    courseTeacherName= course.courseTeacher!!.username,
                    courseThumbnailUrl = course.courseThumbnail,
                    rating = course.avgRating,
                    noOfStudentRated = course.noOfStudentRated,
                    noOfStudentEnrolled = course.noOfStudentEnrolled,
                    tag = course.tag
                )
            }
    }

    override suspend fun getCourseDetails(courseId: String, app: Application): Course? {
        app.log.info(courseId)
        val course = courses.find(Filters.eq("courseId", courseId)).firstOrNull()
        app.log.info(course?.courseTitle)
        return course
    }

    override suspend fun AddCourseToBookmark(courseId: String, userId: String): Boolean {
        return bookmarks.insertOne(
            Bookmark(
                userId = userId,
                courseId = courseId
            )
        ).wasAcknowledged()
    }

    override suspend fun removeCourseFromBookmark(courseId: String, userId: String): Boolean {
        val isDeleted = bookmarks.deleteOne(
             and(
                 eq("courseId", courseId),
                 eq("userId", userId)

             )
        ).wasAcknowledged()
        return isDeleted
    }

    override suspend fun createCourse(course: Course): Boolean {
        return courses.insertOne(course).wasAcknowledged()
    }

    override suspend fun getCoursesForTchr(
        tchrId: String,
        page: Int,
        pageSize: Int,
        app: Application
    ): List<CourseOverview>? {


            val courseOverviews =  courses.find(Filters.eq("courseTeacher.Teacherid", tchrId))
                .skip(page * pageSize)
                .limit(pageSize)
                .toList()
                .map { course ->
                    app.log.info(course.courseTitle)
                    CourseOverview(
                        courseId = course.courseId,
                        courseName = course.courseTitle,
                        courseTeacherName= course.courseTeacher!!.username,
                        courseThumbnailUrl = course.courseThumbnail,
                        rating = course.avgRating,
                        noOfStudentRated = course.noOfStudentRated,
                        noOfStudentEnrolled = course.noOfStudentEnrolled,
                        tag = course.tag
                    )

                }
            return courseOverviews
        }


    override suspend fun updateCourseInfo(courseId: String, imageUrl:String?, courseRequest: CourseRequest): Boolean? {
        val course = courses.find(Filters.eq("courseId", courseId)).firstOrNull()
        if (course != null) {
            return courses.updateOne(
                Filters.eq("courseId", courseId),
                Updates.combine(
                    Updates.set("courseTitle",  courseRequest.courseTitle),
                    Updates.set("description", courseRequest.description),
                    Updates.set("courseThumbnail", imageUrl?: course.courseThumbnail),
                    Updates.set("tag",  courseRequest.tag),
                    Updates.set("duration", courseRequest.duration)
                )

            ).wasAcknowledged()
        }
        else{
            return null
        }
    }

    override suspend fun deleteCourse(courseId: String): Boolean {

        return courses.deleteOne(
            eq("courseId", courseId)
        ).wasAcknowledged()
    }
    override suspend fun getTeacherInfoForCourse(userId: String): UserTchr? {
        val user = users.find(Filters.eq("_id", userId)).firstOrNull()
        if (user != null) {
            return UserTchr(
                username = user.username,
                profileImageUrl = user.profileImageUrl,
                Teacherid = userId
            )
        }
        else{
            return null
        }
    }

    override suspend fun enrollCourse(courseId: String, userId: String, app: Application): Boolean {
        val isEnrollmentNotExists = enrollments.find(
            and(
                eq("courseId", courseId),
                eq("userId", userId)
            )
        ).firstOrNull()
        if (isEnrollmentNotExists==null) {
            return false
        }

        enrollments.insertOne(
            Enrollment(userId,courseId,Date())
        )

        return courses.updateOne(
            eq("courseId", courseId),
            inc("noOfStudentEnrolled", 1)
        ).wasAcknowledged()
    }

    override suspend fun hasEnrolledCourse(courseId: String, userId: String): Boolean {
        val resultt=enrollments.find(
            Filters.and(
                eq("courseId", courseId),
                eq("userId", userId)
            )
        ).firstOrNull()



        if(resultt==null){
            return false
        }
        else{
            return  true
        }
    }

    override suspend fun rateCourse(courseId: String, userId: String, ratingValue: Int, app: Application): Boolean {
        val isRatingNotExists = ratings.find(
            Filters.and(
                eq("courseId", courseId),
                eq("userId", userId)
            )
        ).firstOrNull()

        if (isRatingNotExists==null){
            return false
        }


        val isRatedSuccessful = ratings.insertOne(
            Rating(userId,courseId,ratingValue, dateRating = Date())


        ).wasAcknowledged()

        val avgRating = getAvgRating(courseId, app)

        app.log.info("$avgRating")

        return if (isRatedSuccessful) {
            courses.updateOne(
                eq("courseId", courseId),
                combine(
                    set("avgRating", avgRating),
                    inc("noOfStudentRated", 1)
                )
            ).wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun getAvgRating(courseId: String, app: Application): Double {
        val pipeline = listOf(
            match(eq("courseId", courseId)),
            group(eq("courseId", courseId), avg("avg_rating", "\$ratingValue"))
        )

        val result = ratings.aggregate<Document>(pipeline)

        app.log.info("${result.firstOrNull()?.get("avg_rating")}")

        return result.firstOrNull()?.getDouble("avg_rating") ?: 0.0
    }



}