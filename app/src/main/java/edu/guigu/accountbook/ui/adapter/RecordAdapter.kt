package edu.guigu.accountbook.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.guigu.accountbook.R
import edu.guigu.accountbook.data.model.Record
import edu.guigu.accountbook.databinding.ItemRecordBinding
import edu.guigu.accountbook.util.DateUtils

class RecordAdapter(
    private val onItemClick: (Record) -> Unit,
    private val onItemLongClick: (Record) -> Unit
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    private val records = mutableListOf<Record>()

    fun updateRecords(newRecords: List<Record>) {
        records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = records.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position])
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRecordBinding.bind(itemView)

        fun bind(record: Record) {
            val color = Record.getCategoryColor(record.category)

            binding.tvCategoryIcon.text = record.category.first().toString()
            (binding.tvCategoryIcon.background.mutate() as? android.graphics.drawable.GradientDrawable)
                ?.setColor(color)

            binding.tvCategoryName.text = record.category
            binding.tvDate.text = DateUtils.formatDate(record.date)

            if (!record.note.isNullOrBlank()) {
                binding.tvNote.text = "备注：${record.note}"
                binding.tvNote.visibility = View.VISIBLE
            } else {
                binding.tvNote.visibility = View.GONE
            }

            val isIncome = record.type == Record.TYPE_INCOME
            val prefix = if (isIncome) "+" else "-"
            binding.tvAmount.text = "${prefix}¥${DateUtils.formatAmount(record.amount)}"
            binding.tvAmount.setTextColor(Color.parseColor(if (isIncome) "#2ECC71" else "#E74C3C"))

            itemView.setOnClickListener { onItemClick(record) }
            itemView.setOnLongClickListener { onItemLongClick(record); true }
        }
    }
}