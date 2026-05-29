package edu.guigu.accountbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.databinding.FragmentBillsBinding
import edu.guigu.accountbook.ui.adapter.RecordAdapter
import edu.guigu.accountbook.ui.dialog.AddEditRecordDialog
import edu.guigu.accountbook.ui.viewmodel.RecordViewModel
import edu.guigu.accountbook.util.DateUtils

import java.util.Calendar
import android.app.DatePickerDialog
import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import edu.guigu.accountbook.data.database.AppDatabase


class BillsFragment : Fragment() {

    private var _binding: FragmentBillsBinding? = null
    private var currentFilterStart: Long? = null
    private var currentFilterEnd: Long? = null
    private var currentSearchKeyword: String = ""
    private val binding get() = _binding!!
    private lateinit var viewModel: RecordViewModel
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RecordViewModel::class.java]

        adapter = RecordAdapter(
            onItemClick = { record -> showEditDialog(record) },
            onItemLongClick = { record -> showDeleteConfirmDialog(record) }
        )
        binding.rvRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecords.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            adapter.updateRecords(records)
        }

        binding.fabAdd.setOnClickListener { showAddDialog() }
        setupFilter()
        setupSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** 弹出添加弹窗 */
    private fun showAddDialog() {
        AddEditRecordDialog(
            editRecord = null,
            onSave = { record ->
                viewModel.insert(record)
                Toast.makeText(requireContext(), "记录已添加", Toast.LENGTH_SHORT).show()
            }
        ).show(parentFragmentManager, "AddEditDialog")
    }

    /** 弹出编辑弹窗 */
    private fun showEditDialog(record: Record) {
        AddEditRecordDialog(
            editRecord = record,
            onSave = { updatedRecord ->
                viewModel.update(updatedRecord)
                Toast.makeText(requireContext(), "记录已更新", Toast.LENGTH_SHORT).show()
            }
        ).show(parentFragmentManager, "AddEditDialog")
    }

    /** 删除确认对话框 */
    private fun showDeleteConfirmDialog(record: Record) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除确认")
            .setMessage("确定要删除「${record.category}」的这条记录吗？\n金额：¥${DateUtils.formatAmount(record.amount)}")
            .setPositiveButton("删除") { _, _ ->
                viewModel.delete(record)
                Toast.makeText(requireContext(), "记录已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    private fun setupFilter() {
        binding.chipFilter.setOnClickListener { showMonthPicker() }

        binding.chipFilter.setOnCloseIconClickListener {
            // X 按钮清除筛选
            currentFilterStart = null
            currentFilterEnd = null
            binding.chipFilter.isChecked = false
            binding.chipFilter.text = "筛选月份"
            loadRecords()
        }
    }
    private fun showMonthPicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                // 选中某月，设置该月第一天和最后一天的时间戳
                cal.set(year, month, 1, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                currentFilterStart = cal.timeInMillis

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                currentFilterEnd = cal.timeInMillis

                binding.chipFilter.isChecked = true
                binding.chipFilter.text = "${year}年${month + 1}月"
                loadRecords()
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1
        ).apply {
            // 只显示年月选择器
            datePicker.findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )?.visibility = View.GONE
        }.show()
    }

    private fun loadRecords() {
        viewModel.viewModelScope.launch {
            val records = when {
                // 同时有搜索和筛选
                currentSearchKeyword.isNotBlank() && currentFilterStart != null && currentFilterEnd != null -> {
                    AppDatabase.getInstance(requireContext()).recordDao()
                        .searchRecordsByDateRange(currentSearchKeyword, currentFilterStart!!, currentFilterEnd!!)
                }
                // 只有搜索
                currentSearchKeyword.isNotBlank() -> {
                    AppDatabase.getInstance(requireContext()).recordDao()
                        .searchRecords(currentSearchKeyword)
                }
                // 只有筛选
                currentFilterStart != null && currentFilterEnd != null -> {
                    AppDatabase.getInstance(requireContext()).recordDao()
                        .getRecordsByDateRange(currentFilterStart!!, currentFilterEnd!!)
                }
                // 无搜索无筛选
                else -> {
                    AppDatabase.getInstance(requireContext()).recordDao().getAllRecords()
                }
            }
            adapter.updateRecords(records)
        }
    }
    private fun setupSearch() {
        // 搜索功能实现
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 点击搜索按钮时触发（可选）
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 实时搜索：每输入一个字就过滤
                currentSearchKeyword = newText?.trim() ?: ""
                loadRecords()
                return true
            }
        })
    }
}