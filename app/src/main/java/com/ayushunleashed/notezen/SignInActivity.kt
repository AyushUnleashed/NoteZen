package com.ayushunleashed.notezen

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ayushunleashed.notezen.daos.UserDao
import com.ayushunleashed.notezen.models.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.AccountPicker.newChooseAccountIntent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth= Firebase.auth

// Configure Google Sign In
        //Make google Sign in client first,
        //To make google sign in client we need google sign in options so we create that first.

        //gso builder
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // using gso to make google sign in client.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // when sign in button is clicked , start signIn() function.
        gSignInButton.setOnClickListener {
            signIn()
        }
    }

    // to Sign in You use gi
    private fun signIn() {
        //Toast.makeText(this,"Opened Signin",Toast.LENGTH_SHORT).show()
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //Toast.makeText(this,"On Activity Result",Toast.LENGTH_SHORT).show()
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task);
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        //Toast.makeText(this,"handle Sign In Result",Toast.LENGTH_SHORT).show()
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w("TAG", "Google sign in failed", e)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        //Toast.makeText(this,"firebaseAuthWithGoogle",Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        gSignInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("0TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseuser: FirebaseUser?) {
        //Toast.makeText(this,"Updating UI",Toast.LENGTH_SHORT).show()

        if(firebaseuser!=null)
        {
            val user = firebaseuser.displayName?.let {
                UserModel(firebaseuser.uid,
                    it,firebaseuser.photoUrl.toString())
            }
            val userDao = UserDao()
            userDao.addUsers(user)


            val intent = Intent(this,NoteHome::class.java)
            startActivity(intent);
            //Toast.makeText(this,"Going to Notes",Toast.LENGTH_SHORT).show()
            finish()
        }
        else
        {
            gSignInButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            //Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
        }
    }

}