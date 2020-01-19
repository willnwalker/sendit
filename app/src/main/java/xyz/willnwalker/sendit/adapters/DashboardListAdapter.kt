package xyz.willnwalker.sendit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import xyz.willnwalker.sendit.R
import xyz.willnwalker.sendit.models.Course

class DashboardListAdapter(options: FirestoreRecyclerOptions<Course>): FirestoreRecyclerAdapter<Course, DashboardListAdapter.CourseHolder>(options) {

    override fun onBindViewHolder(holder: CourseHolder, position: Int, course: Course) {
        holder.textView.text = course.name
        holder.instructorView.text = "Instructor: ${course.instructorName}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val courseView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return CourseHolder(courseView)
    }

    inner class CourseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textView: TextView = itemView.findViewById(R.id.className)
        internal var instructorView: TextView = itemView.findViewById(R.id.instructorName)
    }

}