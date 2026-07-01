package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_entries")
data class SaleEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Format: YYYY-MM-DD
    val time: String, // Format: HH:mm
    val category: String,
    val itemName: String,
    val quantity: Double,
    val totalBill: Double,
    val cashPaid: Double,
    val costPrice: Double,
    // Automated Calculations
    val due: Double, // Calculated as [totalBill] - [cashPaid]
    val estimatedProfit: Double, // Calculated as [totalBill] - [costPrice]
    val timestamp: Long = System.currentTimeMillis(),
    val customerName: String = "" // Added for tracking dues by customer
)
