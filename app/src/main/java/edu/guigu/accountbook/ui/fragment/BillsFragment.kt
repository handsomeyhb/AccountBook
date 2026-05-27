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

class BillsFragment : Fragment() {

    private var _binding: FragmentBillsBinding? = null
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
}