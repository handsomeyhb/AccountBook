package edu.guigu.accountbook.data.dao

import androidx.room.*
import edu.guigu.accountbook.data.model.Record

/** 分类汇总数据（用于饼图） */
data class CategorySummary(
    val category: String,
    val total: Double

)
/** 月度收支趋势数据 */
data class MonthlyTrend(
    val month: String,          // "2025-05"
    val income: Double,         // 该月收入
    val expense: Double         // 该月支出
)

@Dao
interface RecordDao {
    /** 按关键词搜索：匹配分类名或备注 */
    @Query("SELECT * FROM records WHERE category LIKE '%' || :keyword || '%' OR note LIKE '%' || :keyword || '%' ORDER BY date DESC")
    suspend fun searchRecords(keyword: String): List<Record>
    /**
     * 按月汇总收支趋势
     */
    @Query("""
    SELECT 
        strftime('%Y-%m', date / 1000, 'unixepoch') AS month,
        SUM(CASE WHEN type = 1 THEN amount ELSE 0 END) AS income,
        SUM(CASE WHEN type = 0 THEN amount ELSE 0 END) AS expense
    FROM records 
    GROUP BY month 
    ORDER BY month ASC
""")
    suspend fun getMonthlyTrend(): List<MonthlyTrend>

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