package com.example.fuelfix.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Button to Manage Petrol Stations
        binding.btnManageStations.setOnClickListener {
            startActivity(Intent(this, ManageStationsActivity::class.java))
        }

        binding.btnManageOrders.setOnClickListener {
            startActivity(Intent(this@AdminActivity,ManageOrdersActivity::class.java))
        }
    }
}
