package xyz.willnwalker.sendit


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.fragment_create_course.*
import xyz.willnwalker.sendit.models.Course
import xyz.willnwalker.sendit.models.SharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class CreateCourseFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_create_course, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_class_button.setOnClickListener {
            val newCourse = Course(
                null,
                course_name_field.text.toString(),
                sharedViewModel.firebaseAuth.currentUser!!.displayName,
                sharedViewModel.firebaseAuth.currentUser!!.uid,
                course_join_code_field.text.toString()
            )
            sharedViewModel.firestoreDatabase.collection("courses").add(newCourse).addOnCompleteListener { taskStatus ->
                if(taskStatus.isSuccessful){
                    taskStatus.result!!.update("courseId", taskStatus.result!!.id)
                    sharedViewModel.userRef.update("enrolledCourses", FieldValue.arrayUnion(taskStatus.result!!.id))
                    MaterialDialog(requireActivity()).show {
                        title(text = "Success!")
                        message(text = "Your new course was created!")
                        positiveButton(text = "Okay")
                        onDismiss { findNavController().navigateUp() }
                    }
                }
                else{
                    MaterialDialog(requireActivity()).show {
                        title(text = "Error")
                        message(text = "Failed to create new course.")
                        positiveButton(text = "Okay")
                        onDismiss { findNavController().navigateUp() }
                    }
                }
            }
        }
    }

}
