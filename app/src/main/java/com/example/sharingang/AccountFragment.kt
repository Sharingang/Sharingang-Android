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
    var resultLauncher: ActivityResultLauncher<Intent>? = null

    private enum class AccountStatus {
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
        binding.logoutButton.visibility = View.GONE
        createLauncher(binding)
        signInSetup(binding)
        return binding.root
    }

    private fun createLauncher(binding: FragmentAccountBinding) {
        resultLauncher =
            registerForActivityResult(
                ActivityResultContracts.
                StartActivityForResult()) { result -> if (result.resultCode == Activity.RESULT_OK) {
                    try {
                        val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
                        "Status: Logged in as \n${account.displayName}\n(${account.email})".also { binding.accountStatus.text = it }
                        updateUI(accountStatus = AccountStatus.LOGGED_IN, binding)
                    } catch (e: ApiException) {
                        updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding)
                        binding.accountStatus.text = getString(R.string.failed_login_message)
                    }
                } else {
                    updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding)
                }
            }
    }

    private fun signInSetup(binding: FragmentAccountBinding) {
        val clientId=
            activity?.resources?.getString(R.string.default_web_client_id);
        val gso =
            GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestServerAuthCode(clientId)
            .requestEmail()
            .build()
        val googleSignInClient = activity?.let {
            GoogleSignIn.getClient(it, gso)
        }!!
        binding.loginButton.setOnClickListener {
            sign_in(googleSignInClient, binding)
        }
        binding.logoutButton.setOnClickListener() { sign_out(signInClient = googleSignInClient, binding) }
    }

    private fun sign_in(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        val toLaunch: Intent = signInClient.signInIntent
        resultLauncher?.launch(toLaunch)
        updateUI(accountStatus = AccountStatus.LOGGED_IN, binding) }

    private fun sign_out(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        signInClient.signOut().addOnCompleteListener(requireActivity()) { updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding) } }

    private fun updateUI(accountStatus: AccountStatus, binding: FragmentAccountBinding) {
        if(accountStatus == AccountStatus.LOGGED_IN) {
            binding.loginButton.visibility = View.GONE
            binding.logoutButton.visibility = View.VISIBLE
        } else { binding.loginButton.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.GONE
            binding.accountStatus.text = getString(R.string.text_logged_out)
        }
    }
}