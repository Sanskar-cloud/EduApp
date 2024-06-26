package com.example.routes

import com.example.data.requests.CreateCommentRequest
import com.example.data.requests.DeleteCommentRequest
import com.example.data.responses.BasicApiResponse
import com.example.service.ActivityService
import com.example.service.CommentService
import com.example.service.LikeService
import com.example.util.ApiResponseMessages
import com.example.util.QueryParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.createComment(
    commentService: CommentService,
    activityService: ActivityService,
    app: Application
) {
    authenticate("auth-eduCo") {
        post("/api/comment/create") {
            val request = call.receiveOrNull<CreateCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId
            when (commentService.createComment(request, userId, app)) {
                is CommentService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.FIELDS_BLANK
                        )
                    )
                }
                is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.COMMENT_TOO_LONG
                        )
                    )
                }
                is CommentService.ValidationEvent.Success -> {
                    activityService.addCommentActivity(
                        byUserId = userId,
                        courseId = request.courseId,
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true,
                        )
                    )
                }
                is CommentService.ValidationEvent.UserNotFound -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = "User not found"
                        )
                    )
                }

                is CommentService.ValidationEvent.ServerError -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = "Server Error to create comment"
                        )
                    )
                }
            }
        }
    }
}

fun Route.getCommentsForCourse(
    commentService: CommentService,
) {
    get("/api/user/course/comments") {
        val courseId = call.parameters[QueryParams.PARAM_COURSE_ID] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val comments = commentService.getCommentsForCourse(courseId, call.userId)

        comments?.let {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = true,
                    data = comments
                )
            )
        }?: kotlin.run {
            call.respond(
                HttpStatusCode.NotFound,
                message = "Comment Details is not available"
            )
            return@get
        }

    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
) {
    authenticate("auth-eduCo") {
        delete("/api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val comment = commentService.getCommentById(request.commentId)
            if (comment?.userId != call.userId) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val deleted = commentService.deleteComment(request.commentId)
            if (deleted) {
                likeService.deleteLikesForParent(request.commentId)
                call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = true))
            } else {
                call.respond(HttpStatusCode.NotFound, BasicApiResponse<Unit>(successful = false))
            }
        }
    }
}