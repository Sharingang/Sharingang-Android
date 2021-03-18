package com.example.sharingang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.example.sharingang.databinding.FragmentSearchBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel


class SearchFragment : Fragment() {

    private val viewModel : ItemsViewModel by activityViewModels()
    private var results = MutableLiveData<ArrayList<Item>>()

    private fun searchList(search_name : String?) : ArrayList<Item>? {
        Log.d("info","Seach function called")
        Log.d("info", "Size of database" + viewModel.items.value?.size)
        if (search_name == null || viewModel.items.value == null) {
            Log.d("Debug", "Something was found to be null")
            return null
        }
        Log.d("Debug", "Got past the null check")
        var searchResults : ArrayList<Item> = ArrayList();
        for (i in viewModel.items.value!!) {
            if(i.title.contains(search_name) || i.description.contains(search_name)){
                searchResults.add(i);
            }
        }

        Log.d("Infor", "Number of elements found: " + searchResults.size)
        return searchResults
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val adapter = viewModel.setupItemAdapter()
        binding.itemList.adapter = adapter;
        results.value = ArrayList<Item>()
        results.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        binding.sflSearchButton.setOnClickListener{
            results.value = searchList(R.id.sflSearchText.toString());
            results.observe(viewLifecycleOwner, {
                it?.let { adapter.submitList(it) }
            })

        }
        return binding.root
    }



}