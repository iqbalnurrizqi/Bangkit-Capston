package com.example.capstoneproject4

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.capstoneproject4.data.local.DataStoreManager
import com.example.capstoneproject4.data.repository.SettingsRepository
import com.example.capstoneproject4.ui.settings.SettingsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menyambungkan NavController dengan FragmentContainerView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup ActionBar dengan NavController
        setupActionBarWithNavController(navController)

        // Menyambungkan BottomNavigationView dengan NavController
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // Inisialisasi DrawerLayout dan NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Setup ActionBarDrawerToggle untuk sinkronisasi dengan hamburger menu
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            findViewById(R.id.toolbar_home),
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Observasi data profil dari ViewModel
        observeProfileData()

        // Menangani item menu di NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingsFragment -> {
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.settingsFragment)
                }

                R.id.menu_logout -> {
                    lifecycleScope.launch {
                        val dataStoreManager = DataStoreManager(this@MainActivity)
                        dataStoreManager.clearSession()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                    }
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.scanFragment -> {
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.scanFragment)
                    true
                }

                R.id.historyFragment -> {
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.historyFragment)
                    true
                }

                else -> false
            }
        }

    }

    private fun observeProfileData() {
        // Observasi data profil untuk update header
        viewModel.profileData.observe(this) { profile ->
            val headerView = navigationView.getHeaderView(0)
            val profileImage = headerView.findViewById<ImageView>(R.id.iv_profile_image)
            val userName = headerView.findViewById<TextView>(R.id.header_title)

            if (profile != null) {
                // Update nama user
                val name = profile.data.data.name ?: "Unknown User"
                userName.text = "Welcome, $name"

                // Update foto profil menggunakan Glide
                val photoUrl = profile.data.data.photo_url.takeIf { it != "N/A" } ?: null
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(profileImage)
            } else {
                // Tampilkan placeholder jika data kosong
                userName.text = "Welcome, User"
                Glide.with(this)
                    .load(R.drawable.ic_placeholder)
                    .into(profileImage)
            }
        }

        // Memuat data profil dari ViewModel
        viewModel.loadUserProfile()
    }

    // Fungsi untuk menyembunyikan Toolbar dan BottomNavigationView
    fun hideToolbarAndBottomNavigation() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        toolbar.visibility = View.GONE
        bottomNavigationView.visibility = View.GONE
    }

    // Fungsi untuk menampilkan Toolbar dan BottomNavigationView
    fun showToolbarAndBottomNavigation() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        toolbar.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE
    }

    // Menangani navigasi ketika toolbar back button ditekan
    override fun onSupportNavigateUp(): Boolean {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}