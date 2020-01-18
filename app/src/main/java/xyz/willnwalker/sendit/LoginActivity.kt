package xyz.willnwalker.sendit

import android.content.Intent
import android.os.Bundle

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.appcompat.app.AppCompatActivity

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirestoreDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
        mGoogleSignInClient.signOut() // Make sure there isn't a user currently signed in, just in case
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirestoreDatabase = FirebaseFirestore.getInstance()
        sign_in_button.setSize(SignInButton.SIZE_WIDE)
        sign_in_button.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View) {
        startActivityForResult(mGoogleSignInClient.signInIntent, 2148)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2148) {
            try{
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            } catch (e: RuntimeExecutionException){
                showSnackbar("Failed to log in: $e")
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            firebaseAuthWithGoogle(completedTask.result!!)
        } catch (e: ApiException) {
            showSnackbar("Failed to log in: $e")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        if (!account.email!!.endsWith("ucsc.edu")) {
            mGoogleSignInClient.signOut()
            showSnackbar("Please log in with your ucsc.edu email.")
        } else {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if(task.result!!.additionalUserInfo!!.isNewUser){
                            val userId = task.result!!.user!!.uid
                            val users = mFirestoreDatabase.collection("users")
                            val userTypes = listOf("Student", "Instructor")
                            MaterialDialog(this).show {
                                title(text = "Are you a Student or an Instructor?")
                                listItemsSingleChoice(items = userTypes){
                                        _, index, _ ->
                                    val user = User(userId, UserType.values()[index])
                                    users.document(userId).set(user)
                                        .addOnSuccessListener { launchDashboard() }
                                        .addOnFailureListener { MaterialDialog(this@LoginActivity).show {
                                            title(text = "Failed to create new account")
                                            message(text = "Error encountered while creating account.")
                                        } }
                                }
                                positiveButton(text = "Create Account")
                                onCancel {
                                    mFirebaseAuth.currentUser!!.delete()
                                    mFirebaseAuth.signOut()
                                    mGoogleSignInClient.signOut()
                                }
                            }
                        }
                        else{
                            // Sign in success, update UI with the signed-in user's information
                            launchDashboard()
                        }
                    } else {
                        showSnackbar("Sign in failed: " + task.exception!!)
                        mGoogleSignInClient.signOut()
                    }
                }
        }
    }

    private fun launchDashboard() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(loginActivity, message, Snackbar.LENGTH_LONG).show()
    }
}
