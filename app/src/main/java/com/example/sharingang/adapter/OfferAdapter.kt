package com.example.sharingang.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sharingang.databinding.ItemOfferBinding
import com.example.sharingang.model.Offer
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class OfferAdapter(query: Query, private val listener: OnOfferSelectedListener) :
    FirestoreAdapter<OfferAdapter.ViewHolder>(query) {

    interface OnOfferSelectedListener {
        fun onOfferSelected(offer: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOfferBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(val binding: ItemOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnOfferSelectedListener?
        ) {
            val offer = snapshot.toObject<Offer>()
            if (offer == null) {
                return
            }

            binding.offerItemTitle.text = offer.title

            // Click listener
            binding.root.setOnClickListener {
                listener?.onOfferSelected(snapshot)
            }
        }
    }
}