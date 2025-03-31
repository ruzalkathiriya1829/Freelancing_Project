package com.example.fuelfix.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fuelfix.Adapter.CalendarAdapter
import com.example.fuelfix.Adapter.TimeAdapter
import com.example.fuelfix.ModalClass.DateModel
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ActivityOrderDetailsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var database: DatabaseReference
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var timeAdapter: TimeAdapter
    private val dateList = mutableListOf<DateModel>()
    private val timeSlots = listOf("8 AM - 11 AM", "11 AM - 2 PM", "2 PM - 5 PM")
    private var quantity: Int = 1  // Default quantity
    private var fuelPrice: Int = 0  // Variable to store fuel price
    private var totalPrice: Int = 0

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val stationName = intent.getStringExtra("station_name") ?: "Unknown"
        val stationImageUrl = intent.getStringExtra("station_image_url")

        // Set station name in UI
        binding.txtStationName.text = stationName

        // Fetch fuel price from Firebase
        fetchFuelPrice(stationName)

        // Load station image with Glide
        if (!stationImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(stationImageUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .into(binding.imgStation)
        }

        // Initialize date & time selection
        generateDates()
        generateTimeSlots()
        quantityOfFuel()

        calendarAdapter = CalendarAdapter(dateList) { selectedPosition ->
            selectedDate = dateList[selectedPosition].date
            Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        binding.rvCalendar.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCalendar.adapter = calendarAdapter

        binding.btnPlaceOrder.setOnClickListener {
            proceedToPayment()
        }

        // Fix for onBackPressed crash
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun quantityOfFuel() {
        binding.btnIncrease.setOnClickListener {
            if (quantity < 10) { // Set a max limit if required
                quantity++
                binding.txtQuantity.text = quantity.toString()
                binding.txtQuantityValue.text = quantity.toString()
                updateTotalPrice()  // Update price when quantity changes
            }
        }

        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) { // Prevent negative or zero quantity
                quantity--
                binding.txtQuantity.text = quantity.toString()
                binding.txtQuantityValue.text = quantity.toString()
                updateTotalPrice()  // Update price when quantity changes
            }
        }
    }

    private fun fetchFuelPrice(stationName: String) {
        database = FirebaseDatabase.getInstance().getReference("PetrolStationsTb")

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (station in snapshot.children) {
                    val name = station.child("name").value.toString()
                    if (name == stationName) {
                        val price = station.child("price").value.toString().toIntOrNull() ?: 0
                        fuelPrice = price  // Store the fetched price
                        updateTotalPrice()  // Update the price initially
                        Log.d("FirebaseData", "Fetched Price: ₹$fuelPrice for $stationName")
                        return@addOnSuccessListener
                    }
                }
                binding.txtPrice.text = "Price: Not Available"
                Log.d("FirebaseData", "Station not found: $stationName")
            } else {
                binding.txtPrice.text = "Price: Not Available"
                Log.d("FirebaseData", "No stations exist in database")
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to fetch fuel price", Toast.LENGTH_SHORT).show()
            Log.e("FirebaseError", "Error fetching price", e)
        }
    }

    private fun updateTotalPrice() {
        totalPrice = quantity * fuelPrice
        binding.txtPrice.text = "₹$totalPrice"  // Update total price
    }

    private fun generateTimeSlots() {
        binding.recyclerViewTimes.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        timeAdapter = TimeAdapter(timeSlots) { selectedTimeSlot ->
            selectedTime = selectedTimeSlot
            Toast.makeText(this, "Selected Time: $selectedTime", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewTimes.adapter = timeAdapter
    }

    private fun generateDates() {
        val calendar = Calendar.getInstance()
        for (i in 0..6) {
            val date = SimpleDateFormat("EEE \ndd", Locale.getDefault()).format(calendar.time)
            dateList.add(DateModel(date))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun proceedToPayment() {
        val stationName = binding.txtStationName.text.toString()

        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this@OrderDetailsActivity, PaymentModeActivity::class.java).apply {
            putExtra("stationName", stationName)
            putExtra("fuelPrice", fuelPrice)
            putExtra("selectedDate", selectedDate)
            putExtra("selectedTime", selectedTime)
            putExtra("quantity", quantity)
            putExtra("totalPrice",totalPrice)
        }
        startActivity(intent)
    }
}