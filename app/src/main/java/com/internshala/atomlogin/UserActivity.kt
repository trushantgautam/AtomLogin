package com.internshala.atomlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var fileUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        auth = FirebaseAuth.getInstance()
        setUserInfo()
        btnClicks()
    }
    private fun btnClicks(){
        val q = findViewById<TextView>(R.id.tv_profile_signOut)
        q.setOnClickListener {
            signOutUser()
        }
        val bu = findViewById<Button>(R.id.btn_profileSaveInfo)
        bu.setOnClickListener {
            saveUserInfo()
        }
        val te = findViewById<ImageView>(R.id.iv_profileImage)
        te.setOnClickListener {
            selectImage()
        }
    }
    private fun setUserInfo(){
        val pe = findViewById<EditText>(R.id.et_profileEmail)
        pe.setText(auth.currentUser?.email)
        val pun = findViewById<EditText>(R.id.et_profileUsername)
        pun.setText(auth.currentUser?.displayName)
        val im = findViewById<ImageView>(R.id.iv_profileImage)
        im.setImageURI(auth.currentUser?.photoUrl)
        fileUri = auth.currentUser?.photoUrl
    }
    private fun saveUserInfo(){
        auth.currentUser?.let {
            val usern = findViewById<EditText>(R.id.et_profileUsername)
            val username = usern.text.toString()
            val userProfilePicture  = fileUri
            val usere = findViewById<EditText>(R.id.et_profileEmail)
            val userEmail = usere.text.toString()
            val update = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(userProfilePicture)
                .build()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    it.updateProfile(update).await()
                    it.updateEmail(userEmail)
                    withContext(Dispatchers.Main){
                        setUserInfo()
                        Toast.makeText(this@UserActivity, "Profile succesfully updated!", Toast.LENGTH_SHORT).show()
                    }

                } catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@UserActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            val i = Intent(this@UserActivity, homeactivity::class.java)
            startActivity(i)
            Toast.makeText(this, "Home page", Toast.LENGTH_SHORT).show()
        }
    }
    private  fun  signOutUser(){
        auth.signOut()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        Toast.makeText(this, "Successfully signed out!", Toast.LENGTH_SHORT).show()
    }
    private fun selectImage(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode){
            Activity.RESULT_OK -> {
                fileUri = data?.data
                val image: ImageView = findViewById(R.id.iv_profileImage)
                image.setImageURI(fileUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}