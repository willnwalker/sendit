package xyz.willnwalker.sendit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
        mGoogleSignInClient.signOut() // Make sure there isn't a user currently signed in, just in case
        mFirebaseAuth = FirebaseAuth.getInstance()

        val logout_btn = findViewById(R.id.toolbarbtn) as Button
        logout_btn.setOnClickListener {
            logoutFlow()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", View.OnClickListener { null }).show()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            showLoginFlow()
        }
    }

    private fun showLoginFlow(){
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun launchPoll() {
        val i = Intent(this, PollActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun logoutFlow() {
//        mFirebaseAuth.currentUser!!.delete()
        mFirebaseAuth.signOut()
        mGoogleSignInClient.signOut()
        showLoginFlow()
    }

}
