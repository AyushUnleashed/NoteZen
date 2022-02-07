package com.ayushunleashed.notezen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import com.ayushunleashed.notezen.models.NotesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_note.*


class EditNote : AppCompatActivity() {

    lateinit var titleR:String
    lateinit var descriptionR:String
    lateinit var noteId:String
    lateinit var db: FirebaseFirestore;
    lateinit var firebaseUser: FirebaseUser;
    lateinit var firebaseAuth: FirebaseAuth;

    var dateClass=DateClass()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)


        settingPreviousFields()

        var dateClass=DateClass()
        dateEditNote.text = dateClass.getDate()

        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        customToolBar()
        updateNoteFAB.setOnClickListener {
            updateNotes()
            val intent = Intent(this,NoteHome::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        finish()
        NavUtils.navigateUpFromSameTask(this)
    }

    fun settingPreviousFields()
    {
        val bundle :Bundle ?=intent.extras
        if (bundle!=null){
            titleR = bundle.getString("title").toString()
            descriptionR = bundle.getString("description").toString()
            noteId = bundle.getString("noteId").toString()

        }
        editTitle.setText(titleR)
        editDescription.setText(descriptionR)
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
        setSupportActionBar(myEditToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun updateNotes() {
        val title = editTitle.text.toString()
        val description = editDescription.text.toString()
        val date= "ed: "+dateClass.getDate()
        val note=NotesModel(title,description,date)

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All Fields are required", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes").document(noteId).set(note).
            addOnSuccessListener {
                //Toast.makeText(this,"Note Updated", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Failed To update Note", Toast.LENGTH_SHORT).show()
            }
        }
    }
}