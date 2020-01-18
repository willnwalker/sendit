package xyz.willnwalker.sendit.models

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Restaurant POJO.
 */
@IgnoreExtraProperties
data class SelectedClass(
    var A: Int = 0,
    var Instructor: String = ""
)
