package com.internshala.atomlogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class homeactivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var fileUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeactivity)
        auth = FirebaseAuth.getInstance()
        btnClicks()
    }
    private fun btnClicks(){
        val q = findViewById<TextView>(R.id.tv_profile_signOut)
        q.setOnClickListener {
            signOutUser()
        }
    }
    private  fun  signOutUser(){
        auth.signOut()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        Toast.makeText(this, "Successfully signed out!", Toast.LENGTH_SHORT).show()
    }
}