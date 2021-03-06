package com.example.sharingang

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createNewItem(view: View) {
        val intent = Intent(this, NewItemActivity::class.java)
        startActivity(intent)
    }
}