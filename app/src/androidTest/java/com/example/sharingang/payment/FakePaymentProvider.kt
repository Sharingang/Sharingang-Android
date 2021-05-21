package com.example.sharingang.payment

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.sharingang.models.Item

class FakePaymentProvider : PaymentProvider {
    override fun initialize(fragment: Fragment, context: Context) {
        // Don't have to initialize anything
    }

    override suspend fun requestPayment(item: Item, quantity: Int): Boolean =
        if (item.id == null || item.price < 0.01) false else paymentStatus.status

    enum class Status(val status: Boolean) {
        ALWAYS_ACCEPT(true), ALWAYS_REJECT(false)
    }

    companion object {
        var paymentStatus: Status = Status.ALWAYS_ACCEPT
    }
}
