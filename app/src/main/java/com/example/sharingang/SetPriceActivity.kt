package com.example.sharingang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class SetPriceActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PRICE = "com.example.sharingang.EXTRA_PRICE"
        var CHOSEN_CURRENCY = "USD"
    }

    private val defaultPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_price)
    }

    fun setPriceSummary(view: View) {
        val intent = Intent(this, SetPriceSummaryActivity::class.java)
        val editTextPrice : EditText = findViewById(R.id.editTextSetPrice)
        val price =
            if(editTextPrice.text.isNullOrBlank()) defaultPrice // if no price has been entered
            else editTextPrice.text.toString().toDouble()

        intent.putExtra(EXTRA_PRICE, "The price you set is: $price $CHOSEN_CURRENCY.")
        startActivity(intent)
    }
}