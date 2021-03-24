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

    private enum class AccountStatus{
        LOGGED_IN,
        LOGGED_OUT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentAccountBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(
                        ApiException::class.java
                    )
                    "Status: Logged in as \n${account.displayName}\n(${account.email})".also { binding.accountStatus.text = it }
                    updateUI(accountStatus = AccountStatus.LOGGED_IN, binding)
                } catch (e: ApiException) {
                    updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding)
                    binding.accountStatus.text = getString(R.string.failed_login_message)
                }
            }
            else {
                updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding)
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(CLIENT_AUTH_KEY)
            .requestServerAuthCode(CLIENT_AUTH_KEY)
            .requestEmail()
            .build()
        val googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!
        binding.loginButton.setOnClickListener {
            sign_in(googleSignInClient, binding)
        }
        binding.logoutButton.setOnClickListener() {
            sign_out(signInClient = googleSignInClient, binding)
        }
        return binding.root
    }

    private fun sign_in(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        resultLauncher.launch(signInClient.signInIntent)
        updateUI(accountStatus = AccountStatus.LOGGED_IN, binding)
    }

    private fun sign_out(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        signInClient.signOut()
            .addOnCompleteListener(requireActivity(), OnCompleteListener<Void?> {
                updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding)
            })
    }

    private fun updateUI(accountStatus: AccountStatus, binding: FragmentAccountBinding) {
        if(accountStatus == AccountStatus.LOGGED_IN) {
            binding.loginButton.visibility = View.GONE
            binding.logoutButton.visibility = View.VISIBLE
        }
        else {
            binding.loginButton.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.GONE
            binding.accountStatus.text = getString(R.string.text_logged_out)
        }
    }
}