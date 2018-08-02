package com.example.james.flatchat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import kotlinx.android.synthetic.main.user_row_new_message.*

class ChatlogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)

        // access items from other activity,
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.username

        listenForMessages()

        chat_recyclerview_chatlog.adapter = adapter

        send_button_chatlog.setOnClickListener {
            performSendMessage()
        }




    }

    class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timeStamp: Long) {
        constructor() : this("", "", "", "", -1)
    }

    private fun listenForMessages(){
        val reference = FirebaseDatabase.getInstance().getReference("/messages")

        reference.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                adapter.add(ChatToItem("hello"))
                adapter.add(ChatToItem("hello"))
                adapter.add(ChatFromItem("hello"))

                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
        })

    }

    private fun performSendMessage(){
        val text = send_edittext_chatlog.text.toString()

        // get uid of this user
        val fromId = FirebaseAuth.getInstance().uid

        // get uid of user being chatted to
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000)

        reference.setValue(chatMessage)
                .addOnSuccessListener {

                }
    }

    class ChatFromItem(val text: String): Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.chat_from_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.chat_textview_fromrow.text = text

        }

    }

    class ChatToItem(val text: String): Item<ViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.chat_to_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.chat_textview_torow.text = text
        }

    }

}
