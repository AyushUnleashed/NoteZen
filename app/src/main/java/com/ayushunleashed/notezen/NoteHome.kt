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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class NoteHome : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser;
    private lateinit var notesArrayList:ArrayList<NotesModel>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db:FirebaseFirestore

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


    override fun onStart() {
        super.onStart()
    }

    private fun setUpRecyclerView()
    {
        notesArrayList = arrayListOf();
        EventChangeListner()
        myAdapter = MyAdapter(notesArrayList)
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
    }

    fun EventChangeListner()
    {
        db = FirebaseFirestore.getInstance()
        db.collection("NoteBook").document(firebaseUser.uid).collection("MyNotes").addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error!=null)
                {
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                else
                {
                    for(dc:DocumentChange in value?.documentChanges!!)
                    {
                        if(dc.type == DocumentChange.Type.ADDED)
                        {
                            notesArrayList.add(dc.document.toObject(NotesModel::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }

        })


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
}



