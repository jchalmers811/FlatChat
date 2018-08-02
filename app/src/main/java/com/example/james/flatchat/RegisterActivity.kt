package com.example.james.flatchat

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var imageUri: Uri? = null

    companion object {
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 0
        private val REQUEST_USE_CAMERA = 1
        //private val REQUEST_TAKE_PHOTO = 2
        private val TAG = "RegisterActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // logic for register button
        register_button_register.setOnClickListener {
            performRegister()
        }

        // finish activity if back to login clicked
        backtologin_textview_register.setOnClickListener {
            finish()
        }

        // logic for select photo button
        selectphoto_button_register.setOnClickListener {
            // set up camera permissions
            setupPermissions()
            // select photo from file system
            selectImageInAlbum()
        }

    }

    // if request to access photos is made
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            // get image uri and turn into bitmap
            imageUri = data.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            // add photo and make button invisible
            selectphoto_imageview_register.setImageBitmap(imageBitmap)
            selectphoto_button_register.alpha = 0f

        }
    }



    // setup camera permissions
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission to record denied")
            // Permission is not granted so request permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_USE_CAMERA)
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

//    private fun takePhoto() {
//        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (intent1.resolveActivity(packageManager) != null) {
//            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
//        }
//    }

    private fun performRegister(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a valid email and password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d(TAG, "Registration succesful with UID: ${it.result.user.uid}")
                    uploadImageToStorage()
                }

                .addOnFailureListener {
                    Log.d(TAG, "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToStorage(){
        if (imageUri == null) return

        val filename = UUID.randomUUID().toString()
        val reference = FirebaseStorage.getInstance().getReference("/images/$filename")

        reference.putFile(imageUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                    reference.downloadUrl
                            .addOnSuccessListener {
                                Log.d("Register", "File location: $it")
                                saveUserToDatabase(it.toString())
                            }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload image: ${it.message}")
                }


    }

    private fun saveUserToDatabase(profileImage: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImage)

        reference.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved data to Firebase database")

                    // launch messages activity
                    val intent = Intent(this, MessagesActivity::class.java)
                    // clear previous activities so back will take user home instead of back to registration
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to save data to database: ${it.message}")
                }

    }

}

