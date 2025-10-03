package com.example.thesis

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class TestFirebaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FirebaseDatabase.getInstance("https://thesis-test-274c6-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        val testRef = database.child("testNode")

        testRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, snapshot.value.toString(), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No data found at testNode", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Firebase read failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
