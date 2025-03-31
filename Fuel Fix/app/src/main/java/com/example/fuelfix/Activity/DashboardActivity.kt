package com.example.fuelfix.Activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fuelfix.Fragment.HomeFragment
import com.example.fuelfix.Fragment.LikeFragment
import com.example.fuelfix.Fragment.OrdersFragment
import com.example.fuelfix.Fragment.ProfileFragment
import com.example.fuelfix.Fragment.SearchFragment
import com.example.fuelfix.Fragment.SettingsFragment
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ActivityDashboardBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load Home Fragment by default
        loadFragment(HomeFragment())

        val window = this.window
        window.statusBarColor = this.resources.getColor(R.color.black)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initview()
        getDataFromLoginProviders()
        getUserLocation()

    }


    /** Fetch User's Current Location **/
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val geocoder = Geocoder(this, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val area =
                            addresses[0].subLocality ?: addresses[0].locality ?: "Unknown Area"
                        findViewById<TextView>(R.id.txtCurrentLocation).text = area
                    } else {
                        findViewById<TextView>(R.id.txtCurrentLocation).text = "Area not found"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    findViewById<TextView>(R.id.txtCurrentLocation).text = "Error fetching area"
                }
            }
        }.addOnFailureListener {
            findViewById<TextView>(R.id.txtCurrentLocation).text = "Location unavailable"
        }
    }


    private fun getDataFromLoginProviders() {
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            // Firebase (simple) login

//            getProfileFromFirebase()

//            updateUI(firebaseUser.displayName, firebaseUser.email, firebaseUser.photoUrl?.toString())
        }
    }

//    private fun getProfileFromFirebase() {
//        auth.currentUser?.let {
//            firebaseDatabase.reference.root.child("UserTb").child("fname")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//
//                        // Access the SharedPreferences in an Activity
//                        sharedPreferences = getSharedPreferences(
//                            "FuelFixSharedPreferences",
//                            AppCompatActivity.MODE_PRIVATE
//                        )
//
//                        // Retrieve values from SharedPreferences
//                        val email = sharedPreferences.getString("email", "email")
//                        val image = sharedPreferences.getString("images", "images")
//
//                        Log.e("TAG", "onDataChange: ===> $email")
//
//                        // Update UI elements with retrieved data
//                        binding.txtEmail.text = email
//
////                        Check if the image path is not null and not an empty string
//                        if (image.isNullOrBlank()) {
////                        Log.e("TAG", "onDataChange: Image is null or empty")
//                            // Set a default image using a drawable resource
//                            Glide.with(this@DashboardActivity).load(R.drawable.user)
//                                .into(binding.profileImage)
//                        } else {
//                            Glide.with(this@DashboardActivity).load(image)
//                                .placeholder(R.drawable.user).error(R.drawable.user)
//                                .into(binding.profileImage)
//                        }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Toast.makeText(this@DashboardActivity, "Failed", Toast.LENGTH_SHORT).show()
//                    }
//                })
//        }
//    }

    private fun initview() {

        firebaseDatabase = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        openDrawer()
        bottomNavigationView()
    }

    private fun openDrawer() {
        //open navigation view
        binding.imgMenu.setOnClickListener {

            binding.AppDrawer.openDrawer(binding.navigationView)
        }

        home()
        profile()
        setting()
        shareApp()
        favourite()
    }

    private fun favourite() {
        binding.navFavourite.setOnClickListener {
            loadFragment(LikeFragment()) // Use fragment transaction to load the fragment
            binding.AppDrawer.closeDrawer(binding.navigationView)
        }
    }

    private fun shareApp() {
        //share app
        binding.navShare.setOnClickListener {

            val a = Intent(Intent.ACTION_SEND)
            a.type = "text/plain"
            a.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
            a.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=com.tripadvisor.tripadvisor&hl=en-IN"
            )
            startActivity(Intent.createChooser(a, "Share URL"))

            binding.AppDrawer.openDrawer(GravityCompat.START)

        }
    }

    private fun setting() {
        //setting
        binding.navSettings.setOnClickListener {
            loadFragment(SettingsFragment()) // Use fragment transaction to load the fragment
            binding.AppDrawer.closeDrawer(binding.navigationView)
        }
    }

    private fun profile() {
        //Profile
        binding.navProfile.setOnClickListener {
            loadFragment(ProfileFragment()) // Use fragment transaction to load the fragment
            binding.AppDrawer.closeDrawer(binding.navigationView)
        }
    }

    private fun home() {
        //home
        binding.navHome.setOnClickListener {

            //  binding.AppDrawer.closeDrawer(binding.navigationView)
            intent = Intent(this@DashboardActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun bottomNavigationView() {
        // Handle Bottom Navigation clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_search -> loadFragment(SearchFragment())
                R.id.nav_orders -> loadFragment(OrdersFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    // Function to load fragment dynamically
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}