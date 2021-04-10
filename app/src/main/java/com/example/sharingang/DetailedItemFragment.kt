package com.example.sharingang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.items.Item
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

class DetailedItemFragment : Fragment() {
    private val args: DetailedItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDetailedItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)

        binding.item = args.item

        binding.shareButton.setOnClickListener { shareItem() }

        return binding.root
    }

    private fun shareItem() {
        val item = args.item
        val link = generateLink(item)
        val shareIntent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link.toString())
            putExtra(Intent.EXTRA_TITLE, item.title + " - " + getString(R.string.app_name))
            type = "text/plain"
        }, null)
        startActivity(shareIntent)
    }

    private fun generateLink(item: Item): Uri {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://sharingang.page.link/item?id=${item.id}")
            domainUriPrefix = "https://sharingang.page.link"
            // Open links with this app on Android
            androidParameters { }
        }

        return dynamicLink.uri
    }
}
