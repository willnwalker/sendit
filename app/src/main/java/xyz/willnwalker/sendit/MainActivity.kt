package xyz.willnwalker.sendit

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

import kotlinx.android.synthetic.main.activity_main.*
import xyz.willnwalker.sendit.adapters.RecyclerAdapter
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
    private lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var query: Query
    lateinit var firestore: FirebaseFirestore
    val requestCode = 99
    private var layoutManager: RecyclerView.LayoutManager? = null
    lateinit var adapter: RecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        checkLocationPermission()

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

        layoutManager = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManager


        FirebaseFirestore.setLoggingEnabled(true)

        // Firestore
        firestore = FirebaseFirestore.getInstance()

        // Get ${LIMIT} restaurants
        query = firestore.collection("classes")
            .orderBy("Instructor", Query.Direction.DESCENDING)



        adapter = object : RecyclerAdapter(query, this@MainActivity) {
            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    recycler_view.visibility = View.GONE
                } else {
                    recycler_view.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                    "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
            }
        }

        recycler_view.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            showLoginFlow()
        }
    }
    override fun onSelected(selected: DocumentSnapshot) {
        // Go to the details page for the selected restaurant
        val intent = Intent(this, PollActivity::class.java)

        Radar.trackOnce { status, location, events, user ->
            // do something with status, location, events, user
            val obj = user?.geofences
            obj?.forEach {
                Toast.makeText(this,"${it.description}", Toast.LENGTH_LONG).show()
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

    private fun logoutFlow() {
//        mFirebaseAuth.currentUser!!.delete()
        mFirebaseAuth.signOut()
        mGoogleSignInClient.signOut()
        showLoginFlow()
    }

    private fun checkLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), requestCode)
    }


}
