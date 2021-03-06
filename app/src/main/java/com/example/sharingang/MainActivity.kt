package com.example.sharingang

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharingang.adapter.OfferAdapter
import com.example.sharingang.databinding.ActivityMainBinding
import com.example.sharingang.model.Offer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(),
    OfferAdapter.OnOfferSelectedListener {

    private lateinit var binding: ActivityMainBinding

    lateinit var adapter: OfferAdapter

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val offer = Offer("Manga Naruto", "Je vends des mangas naruto en très bon état")

        firestore = Firebase.firestore

        query = firestore.collection("offers")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)

        // Add a new document with a generated ID
        firestore.collection("offers")
            .add(offer)
            .addOnSuccessListener { documentReference ->
                Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error adding document", e)
            }

        adapter = OfferAdapter(query, this)

        binding.offersRecycler.layoutManager = LinearLayoutManager(this)
        binding.offersRecycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        adapter.stopListening()
    }

    override fun onOfferSelected(offer: DocumentSnapshot) {
        Snackbar.make(binding.root, "Offer ID: " + offer.id, Snackbar.LENGTH_SHORT).show()
    }
}