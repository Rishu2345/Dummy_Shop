package com.assasement.dummyShop.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed interface AppRoute

@Serializable
data object HomeScreenRoute : AppRoute

@Serializable
data class CourseDetailsRoute(
    val courseJson: String,
) : AppRoute {
    @Transient
    val course: Course = Json.decodeFromString(courseJson)

    companion object {
        fun create(course: Course): CourseDetailsRoute {
            return CourseDetailsRoute(
                courseJson = Json.encodeToString(course),
            )
        }
    }
}

@Serializable
data class LessonPlayerRoute(
    val courseJson: String,
    val lessonIndex: Int,
) : AppRoute {
    @Transient
    val course: Course = Json.decodeFromString(courseJson)

    companion object {
        fun create(course: Course, lessonIndex: Int): LessonPlayerRoute {
            return LessonPlayerRoute(
                courseJson = Json.encodeToString(course),
                lessonIndex = lessonIndex,
            )
        }
    }
}
