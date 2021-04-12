package com.example.sharingang

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sharingang.databinding.FragmentAccountBinding
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var binding: FragmentAccountBinding

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                update(AccountStatus.LOGGED_IN, auth.currentUser!!)
            } else {
                update(AccountStatus.LOGGED_OUT, null)
            }
        }

    private enum class AccountStatus {
        LOGGED_IN,
        LOGGED_OUT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )

        binding.loginButton.setOnClickListener {
            signIn()
        }
        binding.logoutButton.setOnClickListener {
            signOut()
        }

        restoreLoginStatus()

        return binding.root
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        resultLauncher.launch(intent)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                update(AccountStatus.LOGGED_OUT, null)
            }
    }

    private fun update(accountStatus: AccountStatus, user: FirebaseUser?) {
        if (accountStatus == AccountStatus.LOGGED_IN) {
            addUserToDatabase(user!!)
            binding.loginButton.visibility = View.GONE
            binding.logoutButton.visibility = View.VISIBLE
            binding.accountStatus.text = "Status: Logged in as \n${user?.displayName}"
        } else {
            binding.loginButton.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.GONE
            binding.accountStatus.text = getString(R.string.text_logged_out)
        }
    }

    private fun restoreLoginStatus() {
        if (auth.currentUser != null) update(AccountStatus.LOGGED_IN, auth.currentUser)
        else update(AccountStatus.LOGGED_OUT, null)
    }

    private fun addUserToDatabase(user: FirebaseUser) {
        lifecycleScope.launch(Dispatchers.IO) {
            userRepository.add(
                User(
                    id = user.uid,
                    name = user.displayName!!,
                    profilePicture = user.photoUrl?.toString()
                )
            )
        }
    }
}