package edu.guigu.accountbook.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_PATTERN = "yyyy年MM月dd日"

    /** 时间戳 → "2025年05月24日" */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_PATTERN, Locale.CHINA)
        return sdf.format(Date(timestamp))
    }

    /** 金额 → "1234.50"（保留两位小数） */
    fun formatAmount(amount: Double): String {
        return String.format(Locale.CHINA, "%.2f", amount)
    }
}