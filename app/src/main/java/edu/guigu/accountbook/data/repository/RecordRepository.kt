package edu.guigu.accountbook.data.repository

import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.data.dao.RecordDao
import edu.guigu.accountbook.data.model.Record

class RecordRepository(private val dao: RecordDao) {

    suspend fun getAllRecords(): List<Record> = dao.getAllRecords()
    suspend fun getTotalIncome(): Double? = dao.getTotalIncome()
    suspend fun getTotalExpense(): Double? = dao.getTotalExpense()
    suspend fun getCategorySummary(type: Int): List<CategorySummary> = dao.getCategorySummary(type)

    suspend fun insert(record: Record): Long = dao.insert(record)
    suspend fun update(record: Record) = dao.update(record)
    suspend fun delete(record: Record) = dao.delete(record)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}