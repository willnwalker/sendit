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
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_instructor_dashboard.*
import xyz.willnwalker.sendit.adapters.DashboardListAdapter
import xyz.willnwalker.sendit.models.Course
import xyz.willnwalker.sendit.models.SharedViewModel
import xyz.willnwalker.sendit.models.User

/**
 * A simple [Fragment] subclass.
 */
class InstructorDashboardFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var enrollmentListener: ListenerRegistration

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = requireActivity().run { ViewModelProviders.of(this).get(SharedViewModel::class.java) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instructor_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            findNavController().navigate(R.id.createCourseFragment)
        }

        teacherCourseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        teacherCourseRecyclerView.showShimmerAdapter()

        enrollmentListener = sharedViewModel.userRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
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

    override fun onPause() {
        super.onPause()
        enrollmentListener.remove()
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
        teacherCourseRecyclerView.showShimmerAdapter()
        Handler().postDelayed(2000) {
            teacherCourseRecyclerView.adapter = adapter
            teacherCourseRecyclerView.hideShimmerAdapter()
            adapter.notifyDataSetChanged()
        }
    }
}
