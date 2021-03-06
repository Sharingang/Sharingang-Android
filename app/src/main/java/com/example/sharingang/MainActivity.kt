package com.example.sharingang

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sharingang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.description = intent.getStringExtra(Intent.EXTRA_TEXT)
    }

    fun createNewItem(view: View) {
        val intent = Intent(this, NewItemActivity::class.java)
        startActivity(intent)
    }
}