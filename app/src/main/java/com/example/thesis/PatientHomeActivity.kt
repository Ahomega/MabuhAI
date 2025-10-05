package com.example.thesis

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PatientHomeActivity : AppCompatActivity() {

    private lateinit var currentUserUid: String
    private lateinit var linkedUid: String
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home)

        currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference.child("users")

        val tvHeartStatus = findViewById<TextView>(R.id.tvHeartStatus)
        val btnLinkFamily = findViewById<Button>(R.id.buttonLinkFamily)
        val familyEmailInput = findViewById<EditText>(R.id.editTextFamilyEmail)
        val chatBtn = findViewById<Button>(R.id.buttonChat)

        tvHeartStatus.text = "Heart Status: Normal" // temporary placeholder

        btnLinkFamily.setOnClickListener {
            val familyEmailRaw = familyEmailInput.text.toString().trim()
            val familyEmail = familyEmailRaw.lowercase()
            if (familyEmail.isEmpty()) {
                Toast.makeText(this, "Enter family email", Toast.LENGTH_SHORT).show()
            } else {
                linkFamily(familyEmail)
            }
        }

        chatBtn.setOnClickListener {
            if (linkedUid.isEmpty()) {
                Toast.makeText(this, "Link a family member first", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, ChatActivity::class.java))
            }
        }

        // fetch existing linked family
        db.child(currentUserUid).child("linkedFamilyId").get().addOnSuccessListener {
            linkedUid = it.getValue(String::class.java) ?: ""
        }
    }

    private fun linkFamily(familyEmail: String) {
        if (familyEmail.isBlank()) {
            Toast.makeText(this, "Please enter a family email", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Searching for family account...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Ensure email is in the same format used during registration
        val normalizedEmail = familyEmail.trim().lowercase()

        db.orderByChild("email").equalTo(normalizedEmail).get()
            .addOnSuccessListener { snapshot ->
                progressDialog.dismiss()

                if (!snapshot.exists()) {
                    Toast.makeText(this, "No account found for $normalizedEmail", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                // Get the first result
                val familyUid = snapshot.children.first().key
                val familyData = snapshot.children.first().value
                if (familyUid == null || familyData == null) {
                    Toast.makeText(this, "Invalid family data in database", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Debug log
                Log.d("FirebaseLink", "Found family UID: $familyUid")

                val updates = hashMapOf<String, Any>(
                    "/$currentUserUid/linkedFamilyId" to familyUid,
                    "/$familyUid/linkedPatientId" to currentUserUid
                )

                db.updateChildren(updates)
                    .addOnSuccessListener {
                        linkedUid = familyUid
                        Toast.makeText(this, "Family linked successfully!", Toast.LENGTH_SHORT).show()
                        Log.d("FirebaseLink", "Linked $currentUserUid <-> $familyUid")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to link family: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("FirebaseLink", "Error updating links", e)
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error fetching family account: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("FirebaseLink", "Database query failed", e)
            }
    }

}
