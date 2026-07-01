package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<SaleEntry>>

    @Query("SELECT * FROM sales_entries WHERE date LIKE :monthPattern ORDER BY date ASC, time ASC")
    fun getEntriesByMonth(monthPattern: String): List<SaleEntry>

    @Query("SELECT * FROM sales_entries WHERE date = :date ORDER BY time ASC")
    fun getEntriesByDate(date: String): Flow<List<SaleEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: SaleEntry)

    @Update
    suspend fun updateEntry(entry: SaleEntry)

    @Delete
    suspend fun deleteEntry(entry: SaleEntry)
}
