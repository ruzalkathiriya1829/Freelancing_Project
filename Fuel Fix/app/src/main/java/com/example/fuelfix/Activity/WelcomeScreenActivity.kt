package com.example.fuelfix.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.fuelfix.R
import com.example.fuelfix.Adapter.SliderAdapter
import com.example.fuelfix.ModalClass.SliderData
import com.example.fuelfix.databinding.ActivityWelcomeScreenBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var sliderList: ArrayList<SliderData>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
        sharedPreferences = getSharedPreferences("FuelFixSharedPreferences", MODE_PRIVATE)

        checkLoginStatus() // Check login status first

        initView()
    }

    private fun checkLoginStatus() {
        if (auth.currentUser != null) {
            // User is already logged in, go to LocationActivity
            startActivity(Intent(this@WelcomeScreenActivity, LocationActivity::class.java))
            finish()
        }
    }

    private fun initView() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {

            binding.idArrowBtn.setOnClickListener {
                sharedPreferences.edit().putBoolean("skipIntro", true).apply()

                // If user is logged in, go to LocationActivity, otherwise go to SignupActivity
                if (auth.currentUser != null) {
                    startActivity(Intent(this, LocationActivity::class.java))
                } else {
                    startActivity(Intent(this, PhoneAuthActivity::class.java))
                }
                finish()
            }

            sliderList = arrayListOf(
                SliderData(
                    "Never waste time at a gas station again. With Fuel Fix, you can have petrol delivered directly to your vehicle whenever you need it.",
                    "Welcome to FuelFix",
                    R.drawable.intro1
                ),
                SliderData(
                    "Skip the pump and save time. Order fuel in a few taps and get it delivered to your vehicle. Experience the convenience and efficiency of smart fueling today!",
                    "Fuel up faster and smarter",
                    R.drawable.intro2
                ),
                SliderData(
                    "Get high-quality fuel delivered to your vehicle. Tap to start your first delivery!",
                    "Ready to Fuel Up?",
                    R.drawable.intro3
                )
            )

            val viewListener = object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    binding.idTVSlideOne.setTextColor(resources.getColor(if (position == 0) R.color.yellow else R.color.lightgray))
                    binding.idTVSlideTwo.setTextColor(resources.getColor(if (position == 1) R.color.yellow else R.color.lightgray))
                    binding.idTVSlideThree.setTextColor(resources.getColor(if (position == 2) R.color.yellow else R.color.lightgray))
                }

                override fun onPageScrollStateChanged(state: Int) {}
            }

            sliderAdapter = SliderAdapter(this, sliderList)
            binding.idViewPager.adapter = sliderAdapter
            binding.idViewPager.addOnPageChangeListener(viewListener)

        } else {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show()
        }
    }
}