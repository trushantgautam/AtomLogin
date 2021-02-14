package com.internshala.atomlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var firstTimeUser = true
    private var fileUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        buttonClicks()
    }
    private fun buttonClicks(){
        val button = findViewById<Button>(R.id.btn_login)
        button.setOnClickListener {
            firstTimeUser = false
            createOrLoginUser()
        }
        val homebutton = findViewById<Button>(R.id.btn_register)
        homebutton.setOnClickListener {
            firstTimeUser = true
            createOrLoginUser()
        }
        val image: ImageView = findViewById(R.id.iv_profileImage)
        image.setOnClickListener {
            selectImage()
        }
    }
    private fun createOrLoginUser() {
        val email = findViewById<EditText>(R.id.et_emailLogin)
        val x = email.text.toString()
        val password = findViewById<EditText>(R.id.et_passwordLogin)
        val p = password.text.toString()
        if( x.isNotEmpty()&& p.isNotEmpty()){
            GlobalScope.launch (Dispatchers.IO){
                try {
                    if(firstTimeUser){
                        auth.createUserWithEmailAndPassword(x,p).await()
                        auth.currentUser.let {
                            val update = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(fileUri)
                                    .build()
                            it?.updateProfile(update)
                        }?.await()
                    }else{
                        auth.signInWithEmailAndPassword(x,p).await()
                    }
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "You are Now logged in", Toast.LENGTH_SHORT).show()
                        val i = Intent(this@MainActivity , UserActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                } catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun checkIfUserIsLoggedIn(){
        if(auth.currentUser != null){
            val i = Intent(this@MainActivity , homeactivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        checkIfUserIsLoggedIn()
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