package com.example.thesis

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class ElderlyHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elderly_home)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val btnAlert = findViewById<Button>(R.id.btnAlert)
        val btnViewBasic = findViewById<Button>(R.id.btnViewBasic)

        // Mock update - in real app you would fetch latest status from DB or service
        tvStatus.text = "Heart Status: Normal"

        btnAlert.setOnClickListener {
            // Vibrate as an immediate feedback
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(400)
            }
            // Mock alert: show Toast
            Toast.makeText(this, "Help alert sent to caregiver", Toast.LENGTH_SHORT).show()
        }

        btnViewBasic.setOnClickListener {
            // Open a small dialog or activity to show simple readings (implement as needed)
            Toast.makeText(this, "ECG: 72 bpm  |  BP: 120/80", Toast.LENGTH_SHORT).show()
        }
    }
}
