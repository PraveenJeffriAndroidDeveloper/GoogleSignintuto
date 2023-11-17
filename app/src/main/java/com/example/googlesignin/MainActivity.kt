package com.example.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()


        findViewById<Button>(R.id.button).setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id))
                .requestEmail()
                .build()

            val signInClient = GoogleSignIn.getClient(this,options)

            signInClient.signInIntent.also {
                startActivityForResult(it , 0)
            }

        }
    }

    private fun googleAuthForFireBase(account: GoogleSignInAccount)
    {
        val credentials = GoogleAuthProvider.getCredential(account.idToken , null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(this@MainActivity,"SuccessFully Logged In",Toast.LENGTH_SHORT).show()
                }
            }
            catch (e:Exception)
            {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0)
        {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account.let {
                googleAuthForFireBase(it)
            }
        }
    }
}