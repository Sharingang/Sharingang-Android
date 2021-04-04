package com.example.sharingang

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sharingang.databinding.FragmentAccountBinding
import com.example.sharingang.users.User
import com.example.sharingang.users.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {
    var resultLauncher: ActivityResultLauncher<Intent>? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var auth: FirebaseAuth
    lateinit var editor: SharedPreferences.Editor
    @Inject
    lateinit var userRepository: UserRepository


    private enum class AccountStatus {
        LOGGED_IN,
        LOGGED_OUT
    }

    private enum class LoginErrorCode {
        SUCCESS,
        FAILURE,
        SIGNOUT
    }

    @SuppressLint("CommitPrefEdits")
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

        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_user_info),
            Context.MODE_PRIVATE
        )!!
        editor = sharedPreferences.edit()
        editor.apply()
        auth = FirebaseAuth.getInstance()
        restorePreferences(binding)
        createLauncher(binding)
        signInSetup(binding)
        return binding.root
    }

    private fun createLauncher(binding: FragmentAccountBinding) {

        resultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    try {
                        val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            .getResult(ApiException::class.java)

                        "Status: Logged in as \n${account.displayName}\n".also { binding.accountStatus.text = it }
                        updateAccountPreferences(editor, LoginErrorCode.SUCCESS, account)
                        firebaseAuthWithGoogle(account.idToken!!, binding)

                        updateUI(accountStatus = AccountStatus.LOGGED_IN, binding)
                    } catch (e: ApiException) {
                        updateUI(accountStatus = AccountStatus.LOGGED_OUT, binding)
                        binding.accountStatus.text = getString(R.string.failed_login_message)
                        updateAccountPreferences(editor, LoginErrorCode.FAILURE, null)
                    }
                } else {
                    handle_no_signin(AccountStatus.LOGGED_OUT, binding, LoginErrorCode.FAILURE)
                }
            }
        editor.apply()
    }

    private fun signInSetup(binding: FragmentAccountBinding) {
        val clientId=
            activity?.resources?.getString(R.string.default_web_client_id)
        val gso =
            GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId!!)
            .requestServerAuthCode(clientId)
            .requestEmail()
            .build()
        val googleSignInClient = activity?.let {
            GoogleSignIn.getClient(it, gso)
        }!!
        binding.loginButton.setOnClickListener {
            sign_in(googleSignInClient, binding)
        }
        binding.logoutButton.setOnClickListener() { sign_out(
            signInClient = googleSignInClient, binding)
        }

    }

    private fun sign_in(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        val toLaunch: Intent = signInClient.signInIntent
        resultLauncher?.launch(toLaunch)
        updateUI(accountStatus = AccountStatus.LOGGED_IN, binding)

    }

    private fun sign_out(signInClient: GoogleSignInClient, binding: FragmentAccountBinding) {
        signInClient.signOut().addOnCompleteListener(requireActivity()) {
            handle_no_signin(AccountStatus.LOGGED_OUT, binding, LoginErrorCode.SIGNOUT)
        }
    }

    private fun updateUI(accountStatus: AccountStatus, binding: FragmentAccountBinding) {
        if(accountStatus == AccountStatus.LOGGED_IN) {
            binding.loginButton.visibility = View.GONE
            binding.logoutButton.visibility = View.VISIBLE
        } else { binding.loginButton.visibility = View.VISIBLE
            binding.logoutButton.visibility = View.GONE
            binding.accountStatus.text = getString(R.string.text_logged_out)
        }
    }

    private fun restorePreferences(binding: FragmentAccountBinding) {
        val sharedPreferences = context?.getSharedPreferences(
            getString(R.string.preference_user_info),
            Context.MODE_PRIVATE
        )
        if (sharedPreferences != null) {
            if(sharedPreferences.getString(getString(R.string.key_account_name), "").equals("")) {
                updateUI(AccountStatus.LOGGED_OUT, binding)
            }
            else {
                "Status: Logged in as \n${sharedPreferences.getString(
                    getString(R.string.key_account_name), "")}"
                    .also { binding.accountStatus.text = it }
                updateUI(AccountStatus.LOGGED_IN, binding)
            }
        }
        else {
            updateUI(AccountStatus.LOGGED_OUT, binding)
        }
    }

    private fun updateAccountPreferences(
        editor: SharedPreferences.Editor, code: LoginErrorCode,
        account: GoogleSignInAccount?
    ) {
        if(code == LoginErrorCode.FAILURE || code == LoginErrorCode.SIGNOUT) {
            editor.putString(getString(R.string.key_account_uid), "")
            editor.putString(getString(R.string.key_account_name), "")
            editor.putString(getString(R.string.key_account_picture), "")
            editor.putString(getString(R.string.key_account_email), "")
            editor.putString(getString(R.string.key_account_token), "")
            editor.putString(getString(R.string.account_firebase_uid), "")
        }
        else {
            editor.putString(getString(R.string.key_account_uid), account?.id)
            editor.putString(getString(R.string.key_account_name), account?.displayName)
            editor.putString(
                getString(R.string.key_account_picture),
                account?.photoUrl!!.toString()
            )
            editor.putString(getString(R.string.key_account_email), account.email!!.toString())
            editor.putString(getString(R.string.key_account_token), account.idToken)
        }
        editor.apply()
    }

    private fun firebaseAuthWithGoogle(idToken: String, binding: FragmentAccountBinding) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    lifecycleScope.launch(Dispatchers.IO) {
                        userRepository.add(
                            User(
                                id = user?.uid,
                                name = user?.displayName!!,
                                profilePicture = user.photoUrl!!.toString()
                            )
                        )
                    }
                    editor.putString(getString(R.string.account_firebase_uid), user?.uid)
                    editor.apply()
                    updateUI(AccountStatus.LOGGED_IN, binding)
                }
            }
    }

    private fun handle_no_signin(status: AccountStatus, binding: FragmentAccountBinding, loginErrorCode: LoginErrorCode) {
        updateUI(status, binding)
        updateAccountPreferences(editor, loginErrorCode, null)
    }
}