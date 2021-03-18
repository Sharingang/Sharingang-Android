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
import com.example.sharingang.items.ItemsViewModel


class SearchFragment : Fragment() {

    private val viewModel : ItemsViewModel by activityViewModels()
    private val adapter = viewModel.setupItemAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.itemList.adapter = adapter

        viewModel.addObserver(viewLifecycleOwner, adapter)
        binding.sflSearchButton.setOnClickListener{

        }
        return binding.root
    }


    private fun searchList(search_name : String?) : ArrayList<Item>? {
        if (search_name == null || viewModel.items.value == null) {
            return null
        }
        var searchResults : ArrayList<Item> = ArrayList();
        for (i in viewModel.items.value!!) {
            if(i.title.contains(search_name) || i.description.contains(search_name)){
                searchResults.add(i);
            }
        }
        return searchResults
    }
}