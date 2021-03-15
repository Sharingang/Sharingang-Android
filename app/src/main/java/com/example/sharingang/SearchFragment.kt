package com.example.sharingang

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.sharingang.databinding.FragmentItemsListBinding
import com.example.sharingang.databinding.FragmentSearchBinding
import com.example.sharingang.items.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SearchFragment : Fragment() {

    private val viewModel : ItemsViewModel by activityViewModels();
    private val adapter = ItemsAdapter(ItemListener {  });
//    private val repo = FirestoreItemRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        binding.itemList.adapter = adapter;
        viewModel.items.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        binding.sflSearchButton.setOnClickListener{
            searchList(binding.sflSearchText.text.toString());
        }
        return binding.root;
    }


    private fun searchList(search_name : String?){
        Log.d("info","Seach function called");
        if(search_name == null){Log.d("info", "String search was null");}else{
            Log.d("info", search_name);
        }
//        val listItems = repo.getAllItemsLiveData()


//        adapter.submitList(searchResults);
    }

}