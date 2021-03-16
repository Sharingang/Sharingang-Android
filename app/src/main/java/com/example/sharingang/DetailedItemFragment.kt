package com.example.sharingang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.items.ItemsViewModel

class DetailedItemFragment : Fragment() {
    private val viewModel: ItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDetailedItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)

        binding.viewModel = viewModel
        return binding.root
    }
}