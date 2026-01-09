package com.example.kasiruas.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BarangDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(barang: BarangEntity)

    @Query("SELECT * FROM barang")
    fun getAllBarang(): Flow<List<BarangEntity>>

    @Update
    suspend fun update(barang: BarangEntity)
}
