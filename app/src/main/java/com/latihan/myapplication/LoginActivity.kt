package com.latihan.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.latihan.myapplication.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.apply {
            btnSingUp.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SingUpActivity::class.java))
            }

            btnLogin.setOnClickListener {
                validation()
            }
        }
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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success")
                    val user = auth.currentUser

                    reload()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithEmail:failure", task.exception)
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
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}