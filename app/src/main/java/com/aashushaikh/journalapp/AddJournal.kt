package com.aashushaikh.journalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.aashushaikh.journalapp.databinding.ActivityAddJournalBinding
import com.aashushaikh.journalapp.model.Journal
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddJournal : AppCompatActivity() {

    lateinit var binding: ActivityAddJournalBinding

    // Credentials
    var currentUserId: String = ""
    var currentUsername: String = ""

    // Firebase References
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    // Firebase Firestore
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference = db.collection("Journal")
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Toast.makeText(this, "Add Journal", Toast.LENGTH_SHORT).show()
        Log.d("TAGY", "Add Journal")

        storageReference = FirebaseStorage.getInstance().reference
        auth = Firebase.auth
        user = auth.currentUser!!

        if (user == null) {
            Toast.makeText(this, "unable to login correctly! Sign out and try again", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.apply {
            progressBar.visibility = View.INVISIBLE
            if (JournalUser.instance != null) {
                JournalUser.instance?.let {
                    currentUserId = user.uid.toString()
                    currentUsername = user.displayName.toString()
                    postUsername.text = currentUsername
                }
            } else {
                Toast.makeText(this@AddJournal, "Couldn't Get the User Data", Toast.LENGTH_SHORT).show()
            }

            addImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }

            btnSave.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                val title = etPostTitle.text.toString().trim()
                val thoughts = etPostDescription.text.toString().trim()
                if (title.isNotEmpty() && thoughts.isNotEmpty() && this@AddJournal::imageUri.isInitialized) {
                    val filepath = storageReference.child("journal_images").child("my_image_" + Timestamp.now().seconds)
                    filepath.putFile(imageUri).addOnSuccessListener {
                        filepath.downloadUrl.addOnSuccessListener { uri ->
                            val journal = Journal(
                                title,
                                thoughts,
                                uri.toString(),
                                currentUserId,
                                currentUsername,
                                Timestamp.now()
                            )
                            collectionReference.add(journal).addOnSuccessListener {
                                progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this@AddJournal, "Post added successfully", Toast.LENGTH_SHORT).show()
                                val i = Intent(this@AddJournal, JournalList::class.java)
                                startActivity(i)
                                finish()
                            }.addOnFailureListener {
                                progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this@AddJournal, "Failed to add Post", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    progressBar.visibility = View.INVISIBLE
                    Intent(this@AddJournal, JournalList::class.java).also {
                        startActivity(it)
                        finish()
                    }
                } else {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this@AddJournal, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if(data != null){
                imageUri = data.data!!
                binding.postImage.setImageURI(imageUri)
                binding.addImage.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if(auth != null){}
    }
}
