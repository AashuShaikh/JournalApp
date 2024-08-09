package com.aashushaikh.journalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.aashushaikh.journalapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Toast.makeText(this, "Welcome to Journal App", Toast.LENGTH_SHORT).show()
        
        binding.btnCreateAccount.setOnClickListener {
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            loginWithEmailPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val journal = JournalUser.instance!!
                    journal.userId = auth.currentUser!!.uid
                    journal.username = auth.currentUser!!.displayName
                    Toast.makeText(this@MainActivity, "Authentication successful.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, JournalList::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Authentication failed.", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null) {
            val intent = Intent(this@MainActivity, JournalList::class.java)
            startActivity(intent)
        }
    }
}