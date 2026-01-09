package com.example.kasiruas.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barang")
data class BarangEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val harga: Int,
    val stok: Int
)
