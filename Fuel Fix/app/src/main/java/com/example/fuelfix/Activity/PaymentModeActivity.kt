package com.example.fuelfix.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ActivityPaymentModeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.util.UUID

class PaymentModeActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPaymentModeBinding
    private var totalAmount: Int = 0 // Total payable amount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Razorpay SDK
        Checkout.preload(applicationContext)

        // Get total price from Intent
        val totalPrice = intent.getIntExtra("totalPrice", 0)
        binding.tvSubTotal.text = "₹$totalPrice.00"

        // Fees & Discounts
        val deliveryFee = 10
        var discount = 20
        val tax = (totalPrice * 0.05).toInt()
        var expressDeliveryFee = 0

        // Function to calculate total cost
        fun updateTotalCost() {
            totalAmount = totalPrice + deliveryFee + tax - discount + expressDeliveryFee
            binding.tvTotalCost.text = "₹$totalAmount.00"
        }

        // Express Delivery Handling
        binding.cbExpressDelivery.setOnCheckedChangeListener { _, isChecked ->
            expressDeliveryFee = if (isChecked) 50 else 0
            updateTotalCost()
        }

        // Apply Promo Code
        binding.btnApplyPromo.setOnClickListener {
            val promoCode = binding.etPromoCode.text.toString().trim()
            discount = if (promoCode == "FUEL50") 50 else 20
            updateTotalCost()
        }

        // "Pay Now" Button Click
        binding.btnPayNow.setOnClickListener {
            createPayment(totalAmount)
        }

        // Update initial cost
        updateTotalCost()
    }

    // Function to Create and Start Payment
    private fun createPayment(totalAmount: Int) {
        Log.d("TAG", "Payment Process Started: Amount => $totalAmount")

        val amountInPaise = (totalAmount * 100).toInt() // Convert to paise
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_IrdZ4YSYCL2SyG") // Set Razorpay Key
        checkout.setImage(R.drawable.splash) // Set App Logo

        val paymentDetails = JSONObject()
        try {
            paymentDetails.put("name", "FuelFix")
            paymentDetails.put("description", "Fuel Payment")
            paymentDetails.put("currency", "INR")
            paymentDetails.put("amount", amountInPaise)
            paymentDetails.put("prefill.contact", "9925827865")
            paymentDetails.put("prefill.email", "ruzalkathiriya2906@gmail.com")

            // Open Razorpay Checkout Activity
            checkout.open(this, paymentDetails)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // Payment Success Callback
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful! Payment ID: $razorpayPaymentId", Toast.LENGTH_LONG).show()

        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("OrdersTb")

        // Generate a unique 4-digit Order ID
        generateUniqueOrderId(database) { uniqueOrderId ->
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val userId = firebaseUser?.uid ?: "Guest"
            val userName = firebaseUser?.displayName ?: "Unknown User"
            val userEmail = firebaseUser?.email ?: "No Email"

            // Create Order Data
            val orderData = hashMapOf(
                "orderId" to uniqueOrderId,
                "userId" to userId,
                "userName" to userName,
                "userEmail" to userEmail,
                "paymentId" to razorpayPaymentId,
                "totalAmount" to totalAmount,
                "deliveryFee" to 10,
                "tax" to (totalAmount * 0.05).toInt(),
                "discount" to if (binding.etPromoCode.text.toString() == "FUEL50") 50 else 20,
                "finalAmount" to totalAmount,
                "paymentStatus" to "Completed",
                "timestamp" to System.currentTimeMillis()
            )

            // Store Order in Firebase
            database.child(uniqueOrderId).setValue(orderData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Order Stored Successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to store order!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to generate a unique 4-digit Order ID
    private fun generateUniqueOrderId(database: DatabaseReference, callback: (String) -> Unit) {
        val randomOrderId = (1000..9999).random().toString() // Generate a random 4-digit number

        database.child(randomOrderId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // If Order ID exists, generate a new one recursively
                generateUniqueOrderId(database, callback)
            } else {
                // If Order ID is unique, return it
                callback(randomOrderId)
            }
        }.addOnFailureListener {
            // If there's an error, fallback to random Order ID
            callback(randomOrderId)
        }
    }


    // Payment Failure Callback
    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed! $response", Toast.LENGTH_LONG).show()
    }
}


