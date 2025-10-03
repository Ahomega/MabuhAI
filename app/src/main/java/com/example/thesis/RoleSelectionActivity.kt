package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RoleSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val btnElderly = findViewById<Button>(R.id.btnElderly)
        val btnFamily = findViewById<Button>(R.id.btnFamily)

        btnElderly.setOnClickListener {
            val intent = Intent(this, PrivacyNoticeActivity::class.java)
            intent.putExtra("role", "Elderly")
            startActivity(intent)
        }

        btnFamily.setOnClickListener {
            val intent = Intent(this, PrivacyNoticeActivity::class.java)
            intent.putExtra("role", "Family")
            startActivity(intent)
        }
    }
}
