package xyz.willnwalker.sendit


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_main.*
import xyz.willnwalker.sendit.models.SharedViewModel
import xyz.willnwalker.sendit.models.User
import xyz.willnwalker.sendit.models.UserType

class MainActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val appBarConfig = AppBarConfiguration.Builder(R.id.studentDashboardFragment, R.id.teacherDashboardFragment).build()
        NavigationUI.setupActionBarWithNavController(this, findNavController(R.id.nav_host_fragment), appBarConfig)


        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
//        mGoogleSignInClient.signOut() // Make sure there isn't a user currently signed in, just in case

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            showLoginFlow()
        }
        else{
            sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
            sharedViewModel.userRef.get().addOnSuccessListener {documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                showDashboard(user!!)
            }
        }
    }

    private fun showLoginFlow(){
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun showDashboard(user: User){
        val nav = findNavController(R.id.nav_host_fragment)
        when(user.userType){
            UserType.Student -> nav.navigate(R.id.studentDashboardFragment)
            UserType.Instructor -> nav.navigate(R.id.teacherDashboardFragment)
            else -> nav.navigate(R.id.blankFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                showLoginFlow()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}