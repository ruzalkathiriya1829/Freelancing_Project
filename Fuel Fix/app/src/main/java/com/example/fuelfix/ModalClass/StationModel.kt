package com.example.fuelfix.ModalClass

data class StationModel(
    var stationId: String = "",  // ✅ Change to String
    val name: String = "",
    val distance: String = "",
    val rating: String = "",
    var like: Int = 0,
    val imageUrl: String = "",
    val fuelPrice: Int = 0
//    val openingTime: String = "",
//    val closingTime: String = "",
//    val fuelType: List<String> = listOf(),
//    val location: String = ""
)
