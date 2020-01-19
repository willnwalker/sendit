package xyz.willnwalker.sendit.models

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SharedViewModel : ViewModel() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUserId: String = firebaseAuth.currentUser!!.uid
    val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    val userRef = firestoreDatabase.collection("users").document(currentUserId)

}