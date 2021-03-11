package com.example.sharingang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.sharingang.SetPriceActivity.Companion.CHOSEN_CURRENCY

class SetPriceSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_price_summary)
        val textViewSummary: TextView = findViewById(R.id.textViewSetPriceSummary)
        val price = intent.getDoubleExtra(SetPriceActivity.EXTRA_PRICE, SetPriceActivity.DEFAULT_PRICE)
        val newText = "The price you set is: $price $CHOSEN_CURRENCY."
        textViewSummary.text = newText
    }
}