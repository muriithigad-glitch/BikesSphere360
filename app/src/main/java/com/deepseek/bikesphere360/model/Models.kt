package com.deepseek.bikesphere360.model

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "user" // Default role
)

data class Product(
    val id: String = "",
    val name: String = "",
    val type: String = "", // Motorbike, Bicycle, Sparepart
    val subCategory: String = "", // Sports, Road, Electric / Racing, Mountain / Motorbike Sparepart, Bicycle Sparepart
    val price: String = "",
    val color: String = "",
    val imageUrl: String = "",
    val isHotDeal: Boolean = false,
    
    // Bicycle specific
    val gearSpeed: String = "",
    val brand: String = "",
    
    // Motorbike specific
    val engineCapacity: String = "",
    val yearOfMake: String = "",
    val topSpeed: String = ""
)

data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val address: String = "",
    val location: String = "",
    val totalAmount: String = "",
    val paidAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class Appointment(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val productName: String = "",
    val visitDate: String = "",
    val visitTime: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
