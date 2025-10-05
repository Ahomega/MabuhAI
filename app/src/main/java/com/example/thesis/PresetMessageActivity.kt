package com.example.thesis

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class PresetMessageActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val presetMessages = listOf(
        "I need assistance.",
        "I’m feeling dizzy.",
        "Please call me.",
        "Everything is fine.",
        "Time for medication."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preset_messages)

        listView = findViewById(R.id.listViewPresets)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, presetMessages)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedMessage = presetMessages[position]
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("presetMessage", selectedMessage)
            startActivity(intent)
            finish()
        }
    }
}
