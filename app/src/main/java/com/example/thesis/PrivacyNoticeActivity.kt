package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrivacyNoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_notice)

        val role = intent.getStringExtra("role") // Elderly or Family
        val btnAgree = findViewById<Button>(R.id.btnAgree)
        val btnDisagree = findViewById<Button>(R.id.btnDisagree)
        val tvNotice = findViewById<TextView>(R.id.tvNotice)

        // Display privacy notice
        tvNotice.text = "By proceeding, you agree that this app may collect and store health-related data securely in Firebase for use by you and authorized caregivers."

        btnAgree.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
            finish()
        }

        btnDisagree.setOnClickListener {
            finishAffinity() // closes the entire app
        }
    }
}
