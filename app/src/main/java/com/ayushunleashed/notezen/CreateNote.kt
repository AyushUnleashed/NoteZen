package com.ayushunleashed.notezen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.ayushunleashed.notezen.models.NotesModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_note_home.*
import kotlinx.android.synthetic.main.activity_note_home.createNoteFAB
import kotlinx.android.synthetic.main.activity_note_home.myToolbar

class CreateNote : AppCompatActivity() {


    lateinit var firebaseAuth: FirebaseAuth;
    lateinit var firebaseUser: FirebaseUser;
    lateinit var db: FirebaseFirestore;
    lateinit var collectionRefrence: CollectionReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)


        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        customToolBar()

        saveNoteFAB.setOnClickListener {
            addNotes()
            val intent = Intent(this,NoteHome::class.java)
            startActivity(intent)
        }
    }

    fun addNotes() {
        val title = enterTitle.text.toString()
        val description = enterDescription.text.toString()
        val note=NotesModel(title,description)
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else {

            db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes").add(note).
            addOnSuccessListener {
                Toast.makeText(this,"Note Added Obj",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Failed To add Note",Toast.LENGTH_SHORT).show()
            }
        }
    }



        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.createnotemenu, menu)
            return super.onCreateOptionsMenu(menu)
        }


       override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            R.id.deleteButtonTop -> {
                //deleteNote()
                true
            }
            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
       }

//    private fun deleteNote() {
//        db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes").document(document.id)
//    }

    fun customToolBar() {
            setSupportActionBar(myToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false);
            val actionBar = supportActionBar
            actionBar?.setDisplayShowHomeEnabled(true)
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }


    }