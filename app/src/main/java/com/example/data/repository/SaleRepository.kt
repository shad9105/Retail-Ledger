package com.example.data.repository

import com.example.data.database.SaleDao
import com.example.data.database.SaleEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SaleRepository(private val saleDao: SaleDao) {
    val allEntries: Flow<List<SaleEntry>> = saleDao.getAllEntries()

    fun getEntriesByDate(date: String): Flow<List<SaleEntry>> {
        return saleDao.getEntriesByDate(date)
    }

    suspend fun getEntriesByMonth(monthPattern: String): List<SaleEntry> {
        return withContext(Dispatchers.IO) {
            saleDao.getEntriesByMonth(monthPattern)
        }
    }

    suspend fun insert(entry: SaleEntry) {
        withContext(Dispatchers.IO) {
            saleDao.insertEntry(entry)
        }
    }

    suspend fun update(entry: SaleEntry) {
        withContext(Dispatchers.IO) {
            saleDao.updateEntry(entry)
        }
    }

    suspend fun delete(entry: SaleEntry) {
        withContext(Dispatchers.IO) {
            saleDao.deleteEntry(entry)
        }
    }
}
