package edu.guigu.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.data.dao.MonthlyTrend
import edu.guigu.accountbook.data.database.AppDatabase
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.data.repository.RecordRepository
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    // 数据层对象
    private val repository: RecordRepository

    // 内部可修改，外部只读
    private val _allRecords = MutableLiveData<List<Record>>(emptyList())
    val allRecords: LiveData<List<Record>> get() = _allRecords

    private val _totalIncome = MutableLiveData(0.0)
    val totalIncome: LiveData<Double> get() = _totalIncome

    private val _totalExpense = MutableLiveData(0.0)
    val totalExpense: LiveData<Double> get() = _totalExpense

    // 支出分类汇总（给饼图用，Day 5 和进阶 X1 会用到）
    private val _expenseCategorySummary = MutableLiveData<List<CategorySummary>>(emptyList())
    val expenseCategorySummary: LiveData<List<CategorySummary>> get() = _expenseCategorySummary
    /** 按月汇总收支趋势 */
    private val _monthlyTrend = MutableLiveData<List<MonthlyTrend>>(emptyList())

    val monthlyTrend: LiveData<List<MonthlyTrend>> get() = _monthlyTrend

    init {
        val dao = AppDatabase.getInstance(application).recordDao()
        repository = RecordRepository(dao)
        refreshData()
    }

    // 获取数据业务
    private fun refreshData() {
        viewModelScope.launch {
            _allRecords.postValue(repository.getAllRecords())
            _totalIncome.postValue(repository.getTotalIncome() ?: 0.0)
            _totalExpense.postValue(repository.getTotalExpense() ?: 0.0)
            _expenseCategorySummary.postValue(repository.getCategorySummary(Record.TYPE_EXPENSE))

            // ========= 新增这两行：加载月度趋势数据 =========
            val trendResult = repository.getMonthlyTrend()
            _monthlyTrend.postValue(trendResult)
        }
    }
    // 添加数据业务
    fun insert(record: Record) {
        viewModelScope.launch {
            repository.insert(record)
            refreshData()
        }
    }

    // 更新数据业务
    fun update(record: Record) {
        viewModelScope.launch {
            repository.update(record)
            refreshData()
        }
    }

    // 删除数据业务
    fun delete(record: Record) {
        viewModelScope.launch {
            repository.delete(record)
            refreshData()
        }
    }

}