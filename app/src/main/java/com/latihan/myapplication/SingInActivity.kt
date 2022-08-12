package com.latihan.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.latihan.myapplication.databinding.ActivitySinginBinding

class SingInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySinginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.apply {
            btnSingUp.setOnClickListener {
                startActivity(Intent(this@SingInActivity, SingUpActivity::class.java))
            }

            btnSingIn.setOnClickListener {
                validation()
            }
            googleSingIn.iconTint = null
            googleSingIn.setOnClickListener {
                signIn()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun validation() {
        binding.apply {
            when {
                (inputEmail.text?.isNotEmpty()!! && inputPass.text?.isNotEmpty()!!) -> {
                    val email = inputEmail.text.toString()
                    val pass = inputPass.text.toString()
                    login(email, pass)
                }

                else -> Snackbar.make(
                    binding.root as ViewGroup,
                    "Fill The Text Field",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    reload()
                } else {
                    Snackbar.make(
                        binding.root as ViewGroup,
                        "Login Failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

    private fun reload() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("data Intent", account.idToken!!)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Snackbar.make(
                    binding.root as ViewGroup,
                    "Login Failed",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        progressBar(true)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar(false)
                    reload()
                } else {
                    Snackbar.make(
                        binding.root as ViewGroup,
                        "Login Failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun signIn() {
        progressBar(true)
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
        progressBar(false)
    }

    private fun progressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}