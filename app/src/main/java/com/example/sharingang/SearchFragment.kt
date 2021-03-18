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
    private val adapter = setupItemAdapter()


    private fun setupItemAdapter(): ItemsAdapter {
        val onEdit = { item: Item -> viewModel.onEditItemClicked(item) }
        val onView = { item: Item -> viewModel.onViewItem(item) }
        return ItemsAdapter(ItemListener(onEdit, onView))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.itemList.adapter = adapter
        viewModel.items.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
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