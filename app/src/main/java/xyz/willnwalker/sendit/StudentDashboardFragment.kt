package xyz.willnwalker.sendit


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_student_dashboard.*
import xyz.willnwalker.sendit.adapters.DashboardListAdapter
import xyz.willnwalker.sendit.models.Course
import xyz.willnwalker.sendit.models.SharedViewModel
import xyz.willnwalker.sendit.models.User

/**
 * A simple [Fragment] subclass.
 */
class StudentDashboardFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = requireActivity().run { ViewModelProviders.of(this).get(SharedViewModel::class.java) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            MaterialDialog(requireActivity()).show {
                title(text = "Enter your course enrollment code below:")
                input { _, text ->
                    attemptEnrollment(text.toString())
                }
                positiveButton(text = "Enroll")
            }
        }

        studentCourseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentCourseRecyclerView.showShimmerAdapter()

        sharedViewModel.userRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e("xyz.willnwalker.sendit", "Listen failed.", firebaseFirestoreException)
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                loadCourseList(user!!)
            }
        }
    }

    private fun loadCourseList(user: User){
        val courseList = sharedViewModel.firestoreDatabase.collection("courses")
        val userEnrollmentList = user.enrolledCourses!!.toList()
        val query: Query = if(userEnrollmentList.isEmpty()){
            courseList.whereEqualTo("courseId", "lprWhSdQCUY6OhbPnbzk")
        }
        else{
            courseList.whereIn("courseId", userEnrollmentList)
        }
        val options: FirestoreRecyclerOptions<Course> = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(query, Course::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
        val adapter = DashboardListAdapter(options)
        studentCourseRecyclerView.showShimmerAdapter()
        Handler().postDelayed(2000) {
            studentCourseRecyclerView.adapter = adapter
            studentCourseRecyclerView.hideShimmerAdapter()
            adapter.notifyDataSetChanged()
        }
    }

    private fun attemptEnrollment(text: String){
        val course = sharedViewModel.firestoreDatabase.collection("courses").whereEqualTo("enrollmentCode", text).limit(1)
        course.get().addOnCompleteListener { courseSnapshot ->
            if(courseSnapshot.result!!.isEmpty){
                MaterialDialog(requireActivity()).show {
                    title(text = "Error")
                    message(text = "Failed to enroll in course.")
                    positiveButton(text = "Okay")
                }
            }
            else{
                val courseId = courseSnapshot.result!!.documents[0].id
                val courseName = courseSnapshot.result!!.documents[0].get("name")
                sharedViewModel.userRef.update("enrolledCourses", FieldValue.arrayUnion(courseId))
                MaterialDialog(requireActivity()).show {
                    title(text = "Success!")
                    message(text = "You've successfully enrolled in $courseName!")
                    positiveButton(text = "Okay")
                }
            }
        }
    }
}
