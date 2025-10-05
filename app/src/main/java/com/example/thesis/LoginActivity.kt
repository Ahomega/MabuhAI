package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private val databaseUrl =
        "https://thesis-test-274c6-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        emailInput = findViewById(R.id.editTextEmail)
        passwordInput = findViewById(R.id.editTextPassword)
        loginBtn = findViewById(R.id.buttonLogin)
        signupBtn = findViewById(R.id.buttonSignup)

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    val db = FirebaseDatabase.getInstance(databaseUrl).reference.child("users").child(uid)
                    db.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").getValue(String::class.java)
                        when (role) {
                            "Patient" -> {
                                startActivity(Intent(this, PatientHomeActivity::class.java))
                                finish()
                            }
                            "Family" -> {
                                startActivity(Intent(this, FamilyHomeActivity::class.java))
                                finish()
                            }
                            else -> Toast.makeText(this, "Role not assigned", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        signupBtn.setOnClickListener {
            startActivity(Intent(this, PrivacyNoticeActivity::class.java))
        }
    }
}
