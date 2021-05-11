package com.example.sharingang.payment

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.sharingang.items.Item

interface PaymentProvider {
    /**
     * Initialize the payment provider.
     * This function MUST be called in the onCreate method.
     *
     * @param context current context
     * @param fragment current fragment
     */
    fun initialize(fragment: Fragment, context: Context)

    /**
     * Start a payment request
     * This function can interact with the user.
     *
     * @param item item being bought. It must have an ID and a positive price.
     * @return whether the payment was successful
     */
    suspend fun requestPayment(item: Item): Boolean
}
