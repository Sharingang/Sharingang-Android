package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sharingang.databinding.FragmentSearchBinding
import com.example.sharingang.items.ItemsViewModel

class SearchFragment : Fragment() {

    private val viewModel : ItemsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val adapter = viewModel.setupItemAdapter()
        binding.itemSearchList.adapter = adapter;
        viewModel.addSearchObserver(viewLifecycleOwner, adapter)

        binding.sflSearchButton.setOnClickListener{
            viewModel.searchItems(binding.sflSearchText.text.toString())
        }

        return binding.root
    }



}