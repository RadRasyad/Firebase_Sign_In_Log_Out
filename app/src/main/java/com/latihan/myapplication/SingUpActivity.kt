package com.latihan.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.latihan.myapplication.databinding.ActivitySingupBinding

class SingUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.apply {
            btnRegister.setOnClickListener {
                validation()
            }

            btnLogin.setOnClickListener {
                finish()
            }
        }

    }

    private fun validation() {
        binding.apply {
            when {
                (inputUsername.text?.isNotEmpty()!! && inputEmail.text?.isNotEmpty()!! &&
                        inputPass.text?.isNotEmpty()!! && inputConPass.text?.isNotEmpty()!!) -> {

                    val username = inputUsername.text.toString()
                    val email = inputEmail.text.toString()
                    val pass = inputPass.text.toString()

                    if (inputPass.text.toString() == inputConPass.text.toString()) {
                        register(username, email, pass)
                    } else {
                        Snackbar.make(
                            binding.root as ViewGroup,
                            "Passwords are not the same",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                else -> Snackbar.make(
                    binding.root as ViewGroup,
                    "Fill The Text Field",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser

                    if (user != null) {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }
                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Sing Up", "User profile updated.")
                                }
                            }

                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sing Up", "createUserWithEmail:success")
                        finish()
                        Snackbar.make(
                            binding.root as ViewGroup,
                            "Success",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Sing Up", "createUserWithEmail:failure", task.exception)
                    Snackbar.make(
                        binding.root as ViewGroup,
                        "Authentication failed.",
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