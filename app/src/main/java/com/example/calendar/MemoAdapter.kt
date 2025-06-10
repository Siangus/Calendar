package com.example.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemoAdapter(
    private val items: List<MemoItem>,
    private val onItemClick: (MemoItem) -> Unit
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.memoDate)
        val titleView: TextView = itemView.findViewById(R.id.memoTitle)
        val textView: TextView = itemView.findViewById(R.id.memoText)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(items[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val item = items[position]
        holder.dateView.text = item.date
        holder.titleView.text = item.title
        holder.textView.text = item.text
    }

    override fun getItemCount(): Int = items.size
}
