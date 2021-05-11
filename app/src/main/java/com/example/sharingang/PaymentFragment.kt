package com.example.sharingang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sharingang.databinding.FragmentPaymentBinding
import com.google.firebase.functions.FirebaseFunctions
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var paymentSheet: PaymentSheet

    private lateinit var customerId: String
    private lateinit var ephemeralKeySecret: String
    private lateinit var paymentIntentClientSecret: String

    @Inject
    lateinit var firebaseFunctions: FirebaseFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)

        binding.buyButton.isEnabled = false

        PaymentConfiguration.init(requireContext(), STRIPE_PUBLISHABLE_KEY)

        paymentSheet = PaymentSheet(this) { result ->
            onPaymentSheetResult(result)
        }

        binding.buyButton.setOnClickListener {
            presentPaymentSheet()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            fetchInitData("item-id") // TODO use real id
        }

        return binding.root
    }

    private suspend fun fetchInitData(itemId: String) {
        firebaseFunctions.getHttpsCallable("checkout").call(mapOf("itemId" to itemId))
            .continueWith { task ->
                if (task.isSuccessful) {
                    val response = task.result?.data as Map<String, String>
                    customerId = response["customer"]!!
                    ephemeralKeySecret = response["ephemeralKey"]!!
                    paymentIntentClientSecret = response["paymentIntent"]!!

                    binding.buyButton.isEnabled = true
                } else {
                    Toast.makeText(context, "Cannot initialize payment", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("PaymentFragment", "Cannot initialize payment", task.exception)
                }
            }.await()
    }

    private fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = getString(R.string.app_name),
                customer = PaymentSheet.CustomerConfiguration(
                    id = customerId,
                    ephemeralKeySecret = ephemeralKeySecret
                )
            )
        )
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(
                    requireContext(),
                    "Payment Canceled",
                    Toast.LENGTH_LONG
                ).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    requireContext(),
                    "Payment Failed. See logcat for details.",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("App", "Got error: ${paymentResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                Toast.makeText(
                    requireContext(),
                    "Payment Complete",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private companion object {
        // This API key doesn't need to be kept secret and can be published.
        // https://stripe.com/docs/keys
        private const val STRIPE_PUBLISHABLE_KEY =
            "pk_test_51IoT1tDnM7eo5X71p5zafNGXapNCRW5qjABr7Js1TjnNfOz5tlZ9M8iPSVCxZbgoc44kZpLb6JPjwnOVU2o0kI7x00WB6wm6WT"
    }
}
