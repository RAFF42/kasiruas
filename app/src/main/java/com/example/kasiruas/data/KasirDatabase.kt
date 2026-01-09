package com.example.kasiruas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BarangEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KasirDatabase : RoomDatabase() {

    abstract fun barangDao(): BarangDao

    companion object {
        @Volatile
        private var INSTANCE: KasirDatabase? = null

        fun getDatabase(context: Context): KasirDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KasirDatabase::class.java,
                    "kasir_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
