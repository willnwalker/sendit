package xyz.willnwalker.sendit

enum class UserType{
    Student,
    Instructor
}

data class User(
    val uid: String? = null,
    val userType: UserType? = null
)

data class Course(
    val name: String? = null,
    val instructorName: String? = null,
    val instructorUid: String? = null,
    val enrollmentCode: String? = null
)