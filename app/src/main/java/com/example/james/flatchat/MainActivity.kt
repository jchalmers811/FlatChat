package com.example.james.flatchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // access to text
        val username = username_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        // listen for button press
        login_button_login.setOnClickListener {

            // TODO: registration logic
        }

        makeaccount_textview_login.setOnClickListener {

            // create and start intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }


    }
}
