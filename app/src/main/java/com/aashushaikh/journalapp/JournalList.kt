package com.aashushaikh.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aashushaikh.journalapp.databinding.ActivityJournalListBinding
import com.aashushaikh.journalapp.model.Journal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class JournalList : AppCompatActivity() {
    private lateinit var binding: ActivityJournalListBinding

    //Firebase References
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    val db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var journalList: MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.myToolbar)

        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@JournalList)
        }

        journalList = arrayListOf<Journal>()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_signout -> {
                if(user != null && firebaseAuth != null) {
                    firebaseAuth.signOut()
                    val intent = Intent(this@JournalList, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.action_add -> {
                if(user != null && firebaseAuth != null) {
                    Log.d("TAGY", "Add Journal1")
                    val intent = Intent(this@JournalList, AddJournal::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        collectionReference.whereEqualTo("userId", user.uid)
            .get().addOnSuccessListener {
            if(!it.isEmpty) {
                for(document in it){
                    val journal = Journal(
                        document.data["title"].toString(),
                        document.data["thoughts"].toString(),
                        document.data["imageUrl"].toString(),
                        document.data["userId"].toString(),
                        document.data["username"].toString(),
                        document.data["timestamp"] as com.google.firebase.Timestamp
                    )
                    Log.d("TAGY", "Journal: ${journal.imageUrl}")
                    journalList.add(journal)
                    binding.noPostList.visibility = View.GONE
                }
                adapter = JournalRecyclerAdapter(this, journalList)
                binding.recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }else{
                binding.noPostList.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(this@JournalList, "Something went Wrong!", Toast.LENGTH_SHORT).show()
        }

    }
}