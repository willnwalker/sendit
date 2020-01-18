package xyz.willnwalker.sendit

enum class UserType{
    Student,
    Instructor,
    Both
}

data class User(
    val uid: String? = null,
    val userType: UserType? = null
)