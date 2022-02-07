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
import android.accounts.AuthenticatorDescription
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_note.*
import org.w3c.dom.Document
import kotlin.properties.Delegates

class NoteHome : AppCompatActivity(), INotesRVAdapter {
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser;
    private lateinit var notesArrayList:ArrayList<NotesModel>
    public lateinit var myAdapter: MyAdapter
    private  lateinit  var db:FirebaseFirestore
    private var pressedTime:Long = 0

    private lateinit var deletedNote:NotesModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_home)

        customToolBar()

        mAuth= Firebase.auth
        firebaseUser = mAuth.currentUser!!


        createNoteFAB.setOnClickListener{
            val intent = Intent(this,CreateNote::class.java)
            startActivity(intent)
        }
        setUpRecyclerView()

    }

    override fun onBackPressed() {
        // Press back again to exit
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            val snackbar =Snackbar.make(myRecyclerView,"Press back again to exit",Snackbar.LENGTH_SHORT)
            snackbar.apply {
                setBackgroundTint(Color.rgb(23,28,38))
                setTextColor(Color.WHITE)
                show()
            }
        }
        pressedTime = System.currentTimeMillis();
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
        val query = db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes").orderBy("title")
        val recyclerViewOptions =FirestoreRecyclerOptions.Builder<NotesModel>().setQuery(query,NotesModel::class.java).build()
        myAdapter= MyAdapter(recyclerViewOptions,this)
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        //deleting notes by swipping
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(myRecyclerView)
    }


    //variable to perform swipping operations
    var itemTouchHelperCallBack = object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var note =myAdapter.snapshots.getSnapshot(viewHolder.adapterPosition)

            var docReference = note.reference
            deletedNote = note.toObject(NotesModel::class.java)!!

           deleteNote(note.id)
            Snackbar.make(myRecyclerView,"Note Deleted",Snackbar.LENGTH_LONG)
                .setAction("Undo", View.OnClickListener {
                    docReference.set(deletedNote).addOnSuccessListener {
                        //Toast.makeText(this@NoteHome,"Undo success",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this@NoteHome,"Undo Failed",Toast.LENGTH_SHORT).show()
                    }
                }).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun customToolBar()
    {
        setSupportActionBar(myToolbarHome)
        custom_title.setText(myToolbarHome.title)
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

    override fun onItemClicked(note: String,title:String,description: String) {
        //Toast.makeText(this,"You clicked on item",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,EditNote::class.java)
        intent.putExtra("title",title)
        intent.putExtra("description",description)
        intent.putExtra("noteId",note)

        startActivity(intent)
    }

    override fun onItemLongClicked(note: String) {
        // TODO: 08-02-2022  
    }

    fun deleteNote(note: String)
    {
        val db =FirebaseFirestore.getInstance()
        val query = db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes")
        query.document(note)
            .delete().addOnSuccessListener {
                //Toast.makeText(this,"Note Deleted Succesfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Not Deleted",Toast.LENGTH_SHORT).show()
            }
    }
}



