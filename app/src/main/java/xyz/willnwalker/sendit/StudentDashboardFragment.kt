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
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
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

        studentClassRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentClassRecyclerView.showShimmerAdapter()

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
        val query: Query = sharedViewModel.firestoreDatabase.collection("courses").whereIn("courseId", user.enrolledCourses!!.toList())
        val options: FirestoreRecyclerOptions<Course> = FirestoreRecyclerOptions.Builder<Course>()
            .setQuery(query, Course::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
        val adapter = DashboardListAdapter(options)
        studentClassRecyclerView.showShimmerAdapter()
        Handler().postDelayed(2000) {
            studentClassRecyclerView.adapter = adapter
            studentClassRecyclerView.hideShimmerAdapter()
            adapter.notifyDataSetChanged()
        }
    }
}
