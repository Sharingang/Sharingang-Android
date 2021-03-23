package com.example.sharingang

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.sharingang.databinding.FragmentAccountBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener


class AccountFragment : Fragment() {

    private val CLIENT_AUTH_KEY: String = "771023799063-kmve17s9cfu6ckd3kijcv8vdvvdqb58s.apps.googleusercontent.com"
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAccountBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )
        // Inflate the layout for this fragment
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(
                        ApiException::class.java
                    )
                    "Status: Logged in as \n${account.displayName}\n(${account.email})".also { binding.accountStatus.text = it }
                } catch (e: ApiException) {
                    binding.accountStatus.text = getString(R.string.failed_login_message)
                }
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(CLIENT_AUTH_KEY)
            .requestServerAuthCode(CLIENT_AUTH_KEY)
            .requestEmail()
            .build()
        val googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!
        binding.loginButton.setOnClickListener {
            sign_in(googleSignInClient)
        }
        binding.logoutButton.setOnClickListener() {
            sign_out(signInClient = googleSignInClient, binding)
        }
        return binding.root
    }

    private fun sign_in(signInClient: GoogleSignInClient) {

        resultLauncher.launch(signInClient.signInIntent)
    }

    private fun sign_out(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        signInClient.signOut()
            .addOnCompleteListener(requireActivity(), OnCompleteListener<Void?> {
                binding.accountStatus.text = "Logged Out"
            })
    }
}