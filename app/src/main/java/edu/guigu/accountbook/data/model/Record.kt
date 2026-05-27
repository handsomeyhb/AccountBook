package edu.guigu.accountbook.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "type")
    val type: Int,                // 0=支出, 1=收入

    @ColumnInfo(name = "category")
    val category: String,         // 分类名

    @ColumnInfo(name = "amount")
    val amount: Double,           // 金额

    @ColumnInfo(name = "note")
    val note: String? = null,     // 备注

    @ColumnInfo(name = "date")
    val date: Long                // 时间戳
) {
    companion object {
        const val TYPE_EXPENSE = 0
        const val TYPE_INCOME = 1

        val EXPENSE_CATEGORIES = listOf(
            "餐饮" to 0xFFFF6B6B.toInt(),
            "交通" to 0xFF4ECDC4.toInt(),
            "购物" to 0xFF45B7D1.toInt(),
            "娱乐" to 0xFF96CEB4.toInt(),
            "住房" to 0xFFFFEAA7.toInt(),
            "医疗" to 0xFFDDA0DD.toInt(),
            "教育" to 0xFF98D8C8.toInt(),
            "其他" to 0xFFBBBBBB.toInt()
        )

        val INCOME_CATEGORIES = listOf(
            "工资" to 0xFF2ECC71.toInt(),
            "兼职" to 0xFF3498DB.toInt(),
            "理财" to 0xFFF39C12.toInt(),
            "红包" to 0xFFE74C3C.toInt(),
            "其他" to 0xFF95A5A6.toInt()
        )

        fun getCategoriesByType(type: Int): List<String> {
            return if (type == TYPE_EXPENSE) EXPENSE_CATEGORIES.map { it.first }
            else INCOME_CATEGORIES.map { it.first }
        }

        fun getCategoryColor(category: String): Int {
            return (EXPENSE_CATEGORIES + INCOME_CATEGORIES)
                .find { it.first == category }?.second ?: 0xFF999999.toInt()
        }
    }
}