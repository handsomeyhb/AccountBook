package edu.guigu.accountbook.data.dao

import androidx.room.*
import edu.guigu.accountbook.data.model.Record

/** 分类汇总数据（用于饼图） */
data class CategorySummary(
    val category: String,
    val total: Double
)

@Dao
interface RecordDao {

    // ===== 查询 =====

    @Query("SELECT * FROM records ORDER BY date DESC")
    suspend fun getAllRecords(): List<Record>

    @Query("SELECT SUM(amount) FROM records WHERE type = ${Record.TYPE_INCOME}")
    suspend fun getTotalIncome(): Double?

    @Query("SELECT SUM(amount) FROM records WHERE type = ${Record.TYPE_EXPENSE}")
    suspend fun getTotalExpense(): Double?

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getRecordsByDateRange(startDate: Long, endDate: Long): List<Record>

    @Query("SELECT category, SUM(amount) AS total FROM records WHERE type = :type GROUP BY category ORDER BY total DESC")
    suspend fun getCategorySummary(type: Int): List<CategorySummary>

    // ===== 增删改 =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record): Long

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)
}