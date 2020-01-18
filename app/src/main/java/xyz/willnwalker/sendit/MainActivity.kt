package xyz.willnwalker.sendit

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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

}
