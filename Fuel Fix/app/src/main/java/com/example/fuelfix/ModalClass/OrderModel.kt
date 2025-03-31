package com.example.fuelfix.ModalClass

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val paymentId: String = "",
    var paymentStatus: String = "Pending", // New Field (Pending / Completed)
    val totalAmount: Int = 0,
    val deliveryFee: Int = 0,
    val tax: Int = 0,
    val discount: Int = 0,
    val finalAmount: Int = 0,
    val timestamp: Long = 0
)