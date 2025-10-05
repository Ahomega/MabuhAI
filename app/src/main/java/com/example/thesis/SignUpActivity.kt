package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var btnPatient: Button
    private lateinit var btnFamily: Button
    private lateinit var btnSignup: Button

    private var selectedRole: String? = null
    private val databaseUrl =
        "https://thesis-test-274c6-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        emailInput = findViewById(R.id.editTextEmail)
        passwordInput = findViewById(R.id.editTextPassword)
        btnPatient = findViewById(R.id.buttonPatient)
        btnFamily = findViewById(R.id.buttonFamily)
        btnSignup = findViewById(R.id.buttonSignup)

        val defaultColor = getColor(R.color.roleUnselected)
        val selectedColor = getColor(R.color.roleSelected)

        fun updateButtonColors() {
            btnPatient.setBackgroundColor(if (selectedRole == "Patient") selectedColor else defaultColor)
            btnFamily.setBackgroundColor(if (selectedRole == "Family") selectedColor else defaultColor)
        }

        btnPatient.setOnClickListener {
            selectedRole = "Patient"
            updateButtonColors()
        }

        btnFamily.setOnClickListener {
            selectedRole = "Family"
            updateButtonColors()
        }

        btnSignup.setOnClickListener {
            val emailRaw = emailInput.text.toString().trim()
            val email = emailRaw.lowercase() // normalize email
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || selectedRole == null) {
                Toast.makeText(this, "Fill all fields and select a role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    val db = FirebaseDatabase.getInstance(databaseUrl).reference.child("users").child(uid)
                    val userMap = mapOf(
                        "email" to email,
                        "role" to selectedRole
                    )
                    db.setValue(userMap).addOnCompleteListener {
                        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
