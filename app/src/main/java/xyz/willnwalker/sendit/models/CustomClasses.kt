package xyz.willnwalker.sendit.models

enum class UserType{
    Student,
    Instructor
}

data class User(
    val uid: String? = null,
    val userType: UserType? = null,
    val enrolledCourses: ArrayList<String>? = null
)

data class Course(
    val name: String? = null,
    val instructorName: String? = null,
    val instructorUid: String? = null,
    val enrollmentCode: String? = null
)