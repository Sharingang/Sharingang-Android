package com.example.sharingang

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.sharingang.items.ItemRepository
import com.google.android.material.navigation.NavigationView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    @Inject
    lateinit var itemRepository: ItemRepository

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupToolbar(navController: NavController) {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupNavView(navController: NavController) {
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
    }

    private fun setupNavigation(navController: NavController) {
        setupToolbar(navController)
        setupNavView(navController)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNavigation(getNavController())

        handleDeepLink()
    }

    /*
    We don't have an option menu yet
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = getNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
    */

    private fun handleDeepLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deepLink = pendingDynamicLinkData?.link
                val id = deepLink?.getQueryParameter("id")
                if (deepLink?.path == "/item" && id != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val item = itemRepository.get(id)
                        if (item != null) {
                            runOnUiThread {
                                getNavController().navigate(ItemsListFragmentDirections
                                    .actionItemsListFragmentToDetailedItemFragment(item))
                            }
                        }
                    }
                }
            }
    }
}
