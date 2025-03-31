package com.example.fuelfix.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ActivityStationDetailsBinding

class StationDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStationDetailsBinding
    private var selectedFuelType: String = "Petrol" // Default selection
    private var fuelPrice: Int = 0
    private val emergencyNumber = "1033"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent
        val stationName = intent.getStringExtra("name") ?: "Unknown"
        val stationDistance = intent.getStringExtra("distance") ?: "N/A"
        val stationRating = intent.getStringExtra("rating") ?: "N/A"
        val stationImageUrl = intent.getStringExtra("imageUrl")
        fuelPrice = intent.getIntExtra("price", 0) // Get fuel price from intent

        // Set data to UI
        binding.stationName.text = stationName
        binding.stationDistance.text = stationDistance
        binding.stationRating.text = stationRating

        // Load image using Glide
        Glide.with(this)
            .load(stationImageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .into(binding.stationImage)

        // Back button click
        binding.btnBack.setOnClickListener { finish() }

        // Fuel type selection
        binding.btnPetrol.setOnClickListener {
            selectedFuelType = "Petrol"
            updateFuelSelection(true)
        }

        binding.btnDiesel.setOnClickListener {
            selectedFuelType = "Diesel"
            updateFuelSelection(false)
        }

        // Call & SMS functionality
        setupCallAndSMS()

        // Book Now button click
        binding.txtBookNow.setOnClickListener {
            val intent = Intent(this@StationDetailsActivity, OrderDetailsActivity::class.java)
            intent.putExtra("station_name", stationName)
            intent.putExtra("fuel_type", selectedFuelType)
            intent.putExtra("price", fuelPrice)
            intent.putExtra("station_image_url", stationImageUrl) // Pass the image URL
            startActivity(intent)
        }
    }

    // Function to update Fuel selection UI
    private fun updateFuelSelection(isPetrol: Boolean) {
        if (isPetrol) {
            binding.btnPetrol.setBackgroundResource(R.drawable.btn_selected)
            binding.btnDiesel.setBackgroundResource(R.drawable.btn_unselected)
            binding.btnPetrol.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnDiesel.setTextColor(ContextCompat.getColor(this, R.color.black))
        } else {
            binding.btnDiesel.setBackgroundResource(R.drawable.btn_selected)
            binding.btnPetrol.setBackgroundResource(R.drawable.btn_unselected)
            binding.btnDiesel.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnPetrol.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun setupCallAndSMS() {
        // Call Intent with permission check
        binding.txtCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CALL_PHONE), 101
                )
            } else {
                makeCall()
            }
        }

        // SMS Intent
        binding.txtSMS.setOnClickListener {
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$emergencyNumber")
                putExtra("sms_body", "Hello, I need assistance.")
            }
            startActivity(smsIntent)
        }
    }

    private fun makeCall() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$emergencyNumber")
        startActivity(callIntent)
    }

    // Handle Permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall()
            } else {
                Toast.makeText(this, "Permission Denied to make a call", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
