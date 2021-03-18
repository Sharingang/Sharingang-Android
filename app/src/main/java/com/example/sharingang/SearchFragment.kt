package com.example.sharingang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sharingang.databinding.FragmentSearchBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemListener
import com.example.sharingang.items.ItemsAdapter
import com.example.sharingang.items.ItemsViewModel


class SearchFragment : Fragment() {

    private val viewModel : ItemsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val adapter = viewModel.setupItemAdapter()
        binding.itemList.adapter = adapter

        viewModel.addObserver(viewLifecycleOwner, adapter)
        binding.sflSearchButton.setOnClickListener{
            searchList(binding.sflSearchText.text.toString())
        }
        return binding.root
    }


    private fun searchList(search_name : String?){
        Log.d("info","Seach function called")
        if(search_name == null){Log.d("info", "String search was null");}else{
            Log.d("info", search_name)
        }
    }
}