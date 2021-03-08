package com.example.sharingang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sharingang.databinding.ActivityNewItemBinding

class NewItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_item)
    }

    fun createNewItem(view: View) {
        val intent = Intent()
        intent.putExtra(Intent.EXTRA_TEXT, binding.description)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}