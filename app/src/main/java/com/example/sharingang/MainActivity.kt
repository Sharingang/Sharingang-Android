package com.example.sharingang

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.sharingang.items.ItemRepository
import com.example.sharingang.shake.ShakeListener
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

    private lateinit var shakeListener: ShakeListener
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupToolbar(navController: NavController) {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupNavView(navController: NavController) {
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
    }

    private fun setupNavigation(navController: NavController) {
        setupToolbar(navController)
        setupNavView(navController)
    }

    private fun setupShakeListener() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeListener = ShakeListener {
            selectRandomItem()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNavigation(getNavController())

        setupShakeListener()

        handleDeepLink()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(shakeListener)
        super.onPause()
    }

    private fun handleDeepLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let { openDeepLink(it) }
            }
    }

    fun openDeepLink(deepLink: Uri) {
        val id = deepLink.getQueryParameter("id")
        if (deepLink.path == "/item" && id != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val item = itemRepository.get(id)
                if (item != null) {
                    runOnUiThread {
                        getNavController().navigate(
                            ItemsListFragmentDirections
                                .actionItemsListFragmentToDetailedItemFragment(item)
                        )
                    }
                }
            }
        }
    }

    // We need the parameter for the callback
    fun onRandomItem(@Suppress("UNUSED_PARAMETER") item: MenuItem) {
        selectRandomItem()
    }

    private fun selectRandomItem() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allItems = itemRepository.getAll()
            if (allItems.isNotEmpty()) {

                val random = allItems.random()
                lifecycleScope.launch(Dispatchers.Main) {
                    val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                    drawerLayout.close()
                    getNavController().navigate(
                        ItemsListFragmentDirections
                            .actionGlobalDetailedItemFragment(random)
                    )
                }
            }
        }
    }
}
