package com.example.sharingang.payment

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sharingang.R
import com.example.sharingang.items.Item
import com.google.firebase.functions.FirebaseFunctions
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StripePaymentProvider @Inject constructor(
    private val firebaseFunctions: FirebaseFunctions
) : PaymentProvider {
    private lateinit var fragment: Fragment
    private lateinit var context: Context
    private lateinit var paymentSheet: PaymentSheet
    private var continuation: Continuation<Boolean>? = null

    override fun initialize(fragment: Fragment, context: Context) {
        this.fragment = fragment
        this.context = context
        this.paymentSheet = PaymentSheet(fragment) { result ->
            onPaymentSheetResult(result)
        }
    }

    override suspend fun requestPayment(item: Item): Boolean {
        if (item.id == null || item.price < 0.01) {
            return false
        }

        val paymentIntentData = fetchPaymentIntentData(item.id, context) ?: return false
        val publishableKey = paymentIntentData["publishableKey"]!!
        val customerId = paymentIntentData["customer"]!!
        val ephemeralKeySecret = paymentIntentData["ephemeralKey"]!!
        val paymentIntentClientSecret = paymentIntentData["paymentIntent"]!!

        PaymentConfiguration.init(context, publishableKey)

        return suspendCoroutine { continuation ->
            this.continuation = continuation
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                PaymentSheet.Configuration(
                    merchantDisplayName = fragment.getString(R.string.app_name),
                    customer = PaymentSheet.CustomerConfiguration(
                        id = customerId,
                        ephemeralKeySecret = ephemeralKeySecret
                    )
                )
            )
        }
    }

    private suspend fun fetchPaymentIntentData(
        itemId: String,
        context: Context
    ): Map<String, String>? {
        return firebaseFunctions.getHttpsCallable("checkout").call(mapOf("itemId" to itemId))
            .continueWith { task ->
                if (task.isSuccessful) {
                    @Suppress("UNCHECKED_CAST")
                    task.result?.data as? Map<String, String>
                } else {
                    Toast.makeText(context, "Cannot initialize payment", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("PaymentFragment", "Cannot initialize payment", task.exception)
                    null
                }
            }.await()
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(
                    context,
                    "Payment Canceled",
                    Toast.LENGTH_LONG
                ).show()
                continuation?.resume(false)
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    context,
                    "Payment Failed",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("StripePaymentProvider", "Payment error", paymentResult.error)
                continuation?.resume(false)
            }
            is PaymentSheetResult.Completed -> {
                Toast.makeText(
                    context,
                    "Payment Complete",
                    Toast.LENGTH_LONG
                ).show()
                continuation?.resume(true)
            }
        }
    }
}
