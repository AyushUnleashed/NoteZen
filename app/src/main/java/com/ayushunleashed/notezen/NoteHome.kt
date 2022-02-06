package com.ayushunleashed.notezen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.notezen.models.NotesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_note_home.*
import android.accounts.AccountManager
import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.w3c.dom.Document

class NoteHome : AppCompatActivity(), INotesRVAdapter {
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser;
    private lateinit var notesArrayList:ArrayList<NotesModel>
    private lateinit var myAdapter: MyAdapter
    private  lateinit  var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_home)

        customToolBar()

//        val db =FirebaseFirestore.getInstance()
//        collectionRef=db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes")
        mAuth= Firebase.auth
        firebaseUser = mAuth.currentUser!!


        createNoteFAB.setOnClickListener{
            val intent = Intent(this,CreateNote::class.java)
            startActivity(intent)
        }

        setUpRecyclerView()

    }


    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }

    private fun setUpRecyclerView()
    {
        val db =FirebaseFirestore.getInstance()
        val query = db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes")
        val recyclerViewOptions =FirestoreRecyclerOptions.Builder<NotesModel>().setQuery(query,NotesModel::class.java).build()

        myAdapter= MyAdapter(recyclerViewOptions,this)
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val logoutItem = menu?.findItem(R.id.logoutButton)
        return super.onCreateOptionsMenu(menu)
    }

    fun customToolBar()
    {
        setSupportActionBar(myToolbar)
        custom_title.setText(myToolbar.title)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logoutButton -> {
            logOut()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    fun logOut() {
        mAuth.signOut();
        clearOldLogin();


        val intent = Intent(this,SignInActivity::class.java)
        startActivity(intent);
    }

    fun clearOldLogin()
    {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()
    }

    override fun onItemClicked(note: String) {
        Toast.makeText(this,"You clicked on item",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,EditNote::class.java)
        startActivity(intent)
    }

    override fun onItemLongClicked(note: String) {
        deleteNote(note)
    }

    fun deleteNote(note: String)
    {
        val db =FirebaseFirestore.getInstance()
        val query = db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes")
        query.document(note)
            .delete().addOnSuccessListener {
                Toast.makeText(this,"Note Deleted Succesfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Not Deleted",Toast.LENGTH_SHORT).show()
            }
    }
}



