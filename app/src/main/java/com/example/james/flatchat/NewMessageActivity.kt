package com.example.james.flatchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUsers() // function that fetches user data

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    fun fetchUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("/users")
        reference.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<ViewHolder>()
                // cycle through each user
                p0.children.forEach {
                    val user = it.getValue(User::class.java)

                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatlogActivity::class.java)
                    // pass items to new activity
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()

                }

                // after cycling through and adding users to adapter
                recyclerview_newmessage.adapter = adapter
            }

        })
    }

    class UserItem(val user: User): Item<ViewHolder>() {

        // provide layout row
        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }

        // bind data
        override fun bind(viewHolder: ViewHolder, position: Int) {
            // get user name
            viewHolder.itemView.username_textview_userrownewmessage.text = user.username
            // load user profile image
            Picasso.get().load(user.profileImage).into(viewHolder.itemView.porfile_circleImageview_userrownewmessage)
        }


    }
}
