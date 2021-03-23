package com.example.sharingang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.sharingang.databinding.FragmentItemsListBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemListener
import com.example.sharingang.items.ItemsAdapter
import com.example.sharingang.items.ItemsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlin.math.log

class ItemsListFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var loginStatus: TextView
    lateinit var loginButton: Button
    private val RC_SIGN_IN = 9001

    private fun setupNavigation() {
        viewModel.navigateToEditItem.observe(viewLifecycleOwner, { item ->
            item?.let {
                this.findNavController().navigate(
                        ItemsListFragmentDirections.actionItemsListFragmentToEditItemFragment(item)
                )
                viewModel.onEditItemNavigated()
            }
        })
        viewModel.viewingItem.observe(viewLifecycleOwner, {
            if (it) {
                this.findNavController().navigate(
                        ItemsListFragmentDirections.actionItemsListFragmentToDetailedItemFragment()
                )
                viewModel.onViewItemNavigated()
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding: FragmentItemsListBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_items_list, container, false)
        binding.viewModel = viewModel
        loginStatus = binding.loginStatus
        loginButton = binding.loginButton
        binding.newItemButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_itemsListFragment_to_newItemFragment)
        }
        binding.gotoSearchButton.setOnClickListener { view: View -> gotoSearchPage(view) }

        val adapter = viewModel.setupItemAdapter()
        binding.itemList.adapter = adapter
        viewModel.addObserver(viewLifecycleOwner, adapter)
        
        setupNavigation()
        binding.goToMap.setOnClickListener {
            startActivity(Intent(this.activity, MapActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("771023799063-kmve17s9cfu6ckd3kijcv8vdvvdqb58s.apps.googleusercontent.com")
            .requestServerAuthCode("771023799063-kmve17s9cfu6ckd3kijcv8vdvvdqb58s.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!
        loginButton.setOnClickListener {
            signIn()
        }

        return binding.root
    }

    fun gotoSearchPage(view : View){
        view.findNavController().navigate(R.id.action_itemsListFragment_to_searchFragment5)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            loginStatus.text = "Logged in as ${account.displayName}" + "\n (${account.email})"
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

}






