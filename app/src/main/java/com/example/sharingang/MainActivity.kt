package com.example.sharingang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sharingang.databinding.ActivityMainBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsAdapter
import com.example.sharingang.items.ItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ItemsViewModel by viewModels()

    private val resultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val description = result.data?.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                viewModel.addItem(Item("Title", description, listOf(), 0, Date()))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = ItemsAdapter()
        binding.itemList.adapter = adapter
        viewModel.items.observe(this, {
            it?.let {
                adapter.data = it
            }
        })
    }

    fun createNewItem(view: View) {
        val intent = Intent(this, NewItemActivity::class.java)
        resultLauncher.launch(intent)
    }
}