package edu.guigu.accountbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.guigu.accountbook.databinding.FragmentStatisticsBinding
import edu.guigu.accountbook.ui.viewmodel.RecordViewModel
import edu.guigu.accountbook.util.DateUtils

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import edu.guigu.accountbook.data.dao.CategorySummary
import edu.guigu.accountbook.data.dao.MonthlyTrend
import edu.guigu.accountbook.data.model.Record

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ★ 必须用 requireActivity() 才能和 BillsFragment 共享同一个 ViewModel
        viewModel = ViewModelProvider(requireActivity())[RecordViewModel::class.java]

        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeData() {
        // 观察总收入 → 更新显示
        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvIncome.text = "¥${DateUtils.formatAmount(income)}"
            updateBalance()
        }

        // 观察总支出 → 更新显示
        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvExpense.text = "¥${DateUtils.formatAmount(expense)}"
            updateBalance()
        }

//         观察支出分类汇总 → 画饼图
        viewModel.expenseCategorySummary.observe(viewLifecycleOwner) { summary ->
            setupPieChart(summary)
        }

        // 折线图
        viewModel.monthlyTrend.observe(viewLifecycleOwner) { trendList ->
            setupLineChart(trendList)
        }
    }

    /** 计算结余 = 收入 - 支出 */
    private fun updateBalance() {
        val income = viewModel.totalIncome.value ?: 0.0
        val expense = viewModel.totalExpense.value ?: 0.0
        val balance = income - expense
        binding.tvBalance.text = "¥${DateUtils.formatAmount(balance)}"
    }
    /**
     * 配置并渲染饼图
     */
    private fun setupPieChart(summary: List<CategorySummary>) {
        if (summary.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.centerText = "暂无数据"
            return
        }

        val entries = summary.map { PieEntry(it.total.toFloat(), it.category) }

        val dataSet = PieDataSet(entries, "").apply {
            // 每个分类对应的颜色
            colors = summary.map {
                val color = Record.getCategoryColor(it.category)
                Color.rgb(
                    android.graphics.Color.red(color),
                    android.graphics.Color.green(color),
                    android.graphics.Color.blue(color)
                )
            }
            sliceSpace = 3f           // 扇形间距
            valueTextSize = 12f       // 百分比字体大小
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                    return "¥${DateUtils.formatAmount(value.toDouble())}"
                }
            })
        }

        binding.pieChart.apply {
            data = pieData
            centerText = "支出分类"
            setUsePercentValues(true)        // 显示百分比
            description.isEnabled = false    // 关闭右下角描述文字
            legend.textColor = android.graphics.Color.DKGRAY
            isDrawHoleEnabled = true         // 空心圆环
            holeRadius = 40f
            animateY(1000)                   // 动画旋转 1 秒
            invalidate()                     // 刷新
        }
    }
    /** 配置并渲染折线图 */
    private fun setupLineChart(trend: List<MonthlyTrend>) {
        if (trend.isEmpty()) return
        val incomeEntries = mutableListOf<com.github.mikephil.charting.data.Entry>()
        val expenseEntries = mutableListOf<com.github.mikephil.charting.data.Entry>()
        val labels = mutableListOf<String>()

        trend.forEachIndexed { index, item ->
            incomeEntries.add(com.github.mikephil.charting.data.Entry(index.toFloat(), item.income.toFloat()))
            expenseEntries.add(com.github.mikephil.charting.data.Entry(index.toFloat(), item.expense.toFloat()))
            labels.add(item.month)
        }

        val incomeSet = LineDataSet(incomeEntries, "收入").apply {
            color = Color.parseColor("#2ECC71")
            setCircleColor(Color.parseColor("#2ECC71"))
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
        }

        val expenseSet = LineDataSet(expenseEntries, "支出").apply {
            color = android.graphics.Color.parseColor("#E74C3C")
            setCircleColor(Color.parseColor("#E74C3C"))
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
        }

        binding.lineChart.apply {
            data = LineData(incomeSet, expenseSet)
            description.isEnabled = false
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels) {}
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            animateX(800)
            invalidate()
        }
    }
}