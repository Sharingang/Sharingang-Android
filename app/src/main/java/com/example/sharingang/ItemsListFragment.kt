package com.example.sharingang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlin.math.log

class ItemsListFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var loginStatus: TextView // text is changing
    lateinit var loginButton: Button // visibility toggle
    lateinit var logoutButton: Button //visibility toggle
    private val SIGN_IN_CODE: Int = 1337
    private val CLIENT_AUTH_KEY: String = "771023799063-kmve17s9cfu6ckd3kijcv8vdvvdqb58s.apps.googleusercontent.com"

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
        logoutButton = binding.logoutButton
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

        /* Signing In  Below */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_AUTH_KEY)
                .requestServerAuthCode(CLIENT_AUTH_KEY)
                .requestEmail()
                .build()


        mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!

        loginButton.setOnClickListener {
            // Sign in
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(
                    signInIntent, SIGN_IN_CODE
            )
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
        }
        logoutButton.setOnClickListener {
            mGoogleSignInClient.signOut()
            mGoogleSignInClient.revokeAccess()
            loginStatus.text = getString(R.string.text_loggedOut)
            logoutButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
        }

        return binding.root
    }

    fun gotoSearchPage(view: View) {
        view.findNavController().navigate(R.id.action_itemsListFragment_to_searchFragment5)
    }

    /* Below: Sign In/Out Implementation*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(
                        ApiException::class.java
                )
                "Logged in as ${account.displayName}\n(${account.email})".also { loginStatus.text = it }
            } catch (e: ApiException) {
                // Sign in was unsuccessful
                logoutButton.visibility = View.GONE
                loginButton.visibility = View.VISIBLE
            }
        }
    }
}






