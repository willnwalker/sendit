package xyz.willnwalker.sendit.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import io.radar.sdk.Radar
import xyz.willnwalker.sendit.R
import xyz.willnwalker.sendit.models.Course

class DashboardListAdapter(options: FirestoreRecyclerOptions<Course>): FirestoreRecyclerAdapter<Course, DashboardListAdapter.CourseHolder>(options) {
    private var context: Context? = null
    private var view: View? = null
    override fun onBindViewHolder(holder: CourseHolder, position: Int, course: Course) {
        holder.textView.text = course.name
        holder.enterButton.setOnClickListener {
        it ->
            Radar.trackOnce { status, location, events, user ->
                // do something with status, location, events, user
                val obj = user?.geofences
                println(location)
            }
            view!!.findNavController().navigate(R.id.PollCreationFragment)

        }
        holder.instructorView.text = "Instructor: ${course.instructorName}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
        val courseView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        context = parent.context
        view = parent
        return CourseHolder(courseView)
    }

    inner class CourseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textView: TextView = itemView.findViewById(R.id.className)
        internal var instructorView: TextView = itemView.findViewById(R.id.instructorName)
        internal var enterButton: Button = itemView.findViewById(R.id.button)

    }

}