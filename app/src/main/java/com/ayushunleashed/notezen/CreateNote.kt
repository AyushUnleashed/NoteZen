package com.ayushunleashed.notezen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import com.ayushunleashed.notezen.models.NotesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_edit_note.*


class CreateNote : AppCompatActivity() {


    lateinit var firebaseAuth: FirebaseAuth;
    lateinit var firebaseUser: FirebaseUser;
    lateinit var db: FirebaseFirestore;
    lateinit var collectionRefrence: CollectionReference;

    var dateClass=DateClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)


        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        customToolBar()

        dateCreateNote.text = dateClass.getDate()

        saveNoteFAB.setOnClickListener {
            addNotes()
            val intent = Intent(this,NoteHome::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finish()
        NavUtils.navigateUpFromSameTask(this)
    }
    fun addNotes() {
        val title = enterTitle.text.toString()
        val description = enterDescription.text.toString()
        val date= dateClass.getDate()
        val note=NotesModel(title,description,date)
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All Fields are required", Toast.LENGTH_SHORT).show();
        } else {

            db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes").add(note).
            addOnSuccessListener {
                Toast.makeText(this,"Note Added",Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this,"Coming Soon",Toast.LENGTH_SHORT).show()
            true
        }
        R.id.pinNoteButtonTop -> {
            Toast.makeText(this,"Coming Soon",Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun customToolBar() {
        setSupportActionBar(myCreateToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    }