package com.example.sharingang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.w3c.dom.Text

class SetPriceSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_price_summary)
        val textViewSummary: TextView = findViewById(R.id.textViewSetPriceSummary)
        textViewSummary.text = intent.getStringExtra(SetPriceActivity.EXTRA_PRICE)
    }
}