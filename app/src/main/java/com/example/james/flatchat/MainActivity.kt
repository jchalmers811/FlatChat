package com.example.james.flatchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // listen for button press
        login_button_login.setOnClickListener {

            performLogin()

        }

        makeaccount_textview_login.setOnClickListener {

            // create and start intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }


    }

    private fun performLogin() {

        // access to text
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    //else
                    Log.d("Main", "Login Successful with UID: ${it.result.user.uid}")
                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to login: ${it.message}")
                    Toast.makeText(this, "Failed to login: ${it.message}", Toast.LENGTH_SHORT).show()
                }




    }


}
