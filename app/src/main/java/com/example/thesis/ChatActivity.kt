package com.example.thesis

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var presetMessagesLayout: LinearLayout
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentUserUid: String
    private var linkedUid: String = ""
    private lateinit var database: DatabaseReference
    private var isPatientMode = false
    private val messageList = mutableListOf<Message>()
    private lateinit var presetScroll: HorizontalScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        presetMessagesLayout = findViewById(R.id.presetMessagesLayout)

        currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        chatAdapter = ChatAdapter(messageList, currentUserUid)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter
        presetScroll = findViewById(R.id.presetMessagesScroll)

        fetchUserRoleAndLink()
        setupSendButton()
    }

    private fun fetchUserRoleAndLink() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserUid)
        userRef.get().addOnSuccessListener { snapshot ->
            val role = snapshot.child("role").getValue(String::class.java)
            when (role) {
                "Patient" -> {
                    isPatientMode = true
                    linkedUid = snapshot.child("linkedFamilyId").getValue(String::class.java) ?: ""
                    presetScroll.visibility = View.VISIBLE
                    setupPresetMessages()

                }
                "Family" -> {
                    isPatientMode = false
                    linkedUid = snapshot.child("linkedPatientId").getValue(String::class.java) ?: ""
                    presetMessagesLayout.visibility = View.GONE
                }
                else -> {
                    Toast.makeText(this, "Role not assigned", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
            }

            if (linkedUid.isNotEmpty()) {
                val chatPath = if (currentUserUid < linkedUid)
                    "${currentUserUid}_$linkedUid"
                else
                    "${linkedUid}_$currentUserUid"

                Log.d("ChatActivity", "Chat path: $chatPath")
                database = FirebaseDatabase.getInstance().reference.child("chats").child(chatPath)
                listenForMessages()
            } else {
                Toast.makeText(this, "No linked user yet", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to fetch user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Create nice preset message buttons for elderly users */
    private fun setupPresetMessages() {
        presetMessagesLayout.visibility = View.VISIBLE
        val presets = listOf(
            "I need assistance.",
            "I’m feeling dizzy.",
            "Please call me.",
            "Everything is fine.",
            "Time for medication."
        )

        presetMessagesLayout.removeAllViews()

        presets.forEach { text ->
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 0, 12, 0)
            }

            val button = Button(this).apply {
                this.text = text
                setBackgroundResource(android.R.drawable.btn_default)
                setPadding(40, 20, 40, 20)
                setOnClickListener { sendMessage(text) }
            }

            presetMessagesLayout.addView(button, params)
        }
    }

    private fun setupSendButton() {
        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                messageInput.text.clear()
                sendMessage(text)
            }
        }
    }

    private fun sendMessage(text: String) {
        if (linkedUid.isEmpty()) {
            Toast.makeText(this, "Cannot send message: no linked user", Toast.LENGTH_SHORT).show()
            return
        }
        val messageId = database.push().key ?: return
        database.child(messageId)
            .setValue(Message(text, currentUserUid))
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun listenForMessages() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                chatAdapter.addMessage(message)
                chatRecyclerView.scrollToPosition(messageList.size - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Chat listener cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
