package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signupBtn: Button
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        userRole = intent.getStringExtra("role")

        emailInput = findViewById(R.id.editTextEmail)
        passwordInput = findViewById(R.id.editTextPassword)
        signupBtn = findViewById(R.id.buttonSignup)

        signupBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = mAuth.currentUser?.uid
                        val database = FirebaseDatabase.getInstance(
                            "https://thesis-test-274c6-default-rtdb.asia-southeast1.firebasedatabase.app/"
                        ).reference.child("users").child(uid!!)

                        val userMap = mapOf(
                            "email" to email,
                            "role" to userRole
                        )

                        database.setValue(userMap).addOnCompleteListener {
                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()

                            // Redirect based on role
                            when (userRole) {
                                "Elderly" -> startActivity(Intent(this, ElderlyHomeActivity::class.java))
                                "Family" -> startActivity(Intent(this, FamilyHomeActivity::class.java))
                                else -> startActivity(Intent(this, LoginActivity::class.java))
                            }
                            finish()
                        }
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        Toast.makeText(this, "Sign up failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
