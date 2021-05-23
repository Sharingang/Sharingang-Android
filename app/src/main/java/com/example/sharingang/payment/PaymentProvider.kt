package com.example.sharingang.payment

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.sharingang.models.Item

/**
 * PaymentProvider enables to collect a payment from the user.
 *
 * Important: Call the initialize method in the onCreate of the fragment.
 */
interface PaymentProvider {
    /**
     * Initialize the payment provider.
     * This function MUST be called in the onCreate method.
     *
     * @param fragment current fragment
     * @param context current context
     */
    fun initialize(fragment: Fragment, context: Context)

    /**
     * Start a payment request
     * This function can interact with the user.
     *
     * @param item item being bought. It must have an ID and a positive price.
     * @return whether the payment was successful
     */
    suspend fun requestPayment(item: Item, quantity: Int): Boolean
}
