package xyz.willnwalker.sendit

import android.Manifest
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
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import io.radar.sdk.Radar
import io.radar.sdk.RadarTrackingOptions
import java.util.*


class MainActivity : AppCompatActivity(),
    RecyclerAdapter.OnSelectedListener{

    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedViewModel: SharedViewModel


    val requestCode = 99
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        checkLocationPermission()

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

        startActivity(intent)
    }
    public override fun onStart() {
        super.onStart()
        adapter.setQuery(query)
        // Start listening for Firestore updates
        adapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        adapter.stopListening()
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
    private fun checkLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), requestCode)
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
