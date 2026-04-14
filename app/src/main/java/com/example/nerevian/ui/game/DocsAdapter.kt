package com.example.nerevian.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
sealed class DocListItem {
    data class Header(val orderRef: String, val route: String) : DocListItem()
    data class File(val docName: String, val status: String, val size: String, val isAvailable: Boolean) : DocListItem()
}

class DocsAdapter(private var items: List<DocListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_FILE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DocListItem.Header -> TYPE_HEADER
            is DocListItem.File -> TYPE_FILE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_doc_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_doc_file, parent, false)
            FileViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as DocListItem.Header)
            is FileViewHolder -> holder.bind(item as DocListItem.File)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<DocListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderRef: TextView = itemView.findViewById(R.id.tvOrderRef)
        private val tvOrderRoute: TextView = itemView.findViewById(R.id.tvOrderRoute)

        fun bind(header: DocListItem.Header) {
            tvOrderRef.text = header.orderRef
            tvOrderRoute.text = header.route
        }
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDocName: TextView = itemView.findViewById(R.id.tvDocName)
        private val tvDocStatus: TextView = itemView.findViewById(R.id.tvDocStatus)
        private val ivDocAction: ImageView = itemView.findViewById(R.id.ivDocAction)

        fun bind(file: DocListItem.File) {
            tvDocName.text = file.docName
            tvDocStatus.text = file.status


            if (file.isAvailable) {
                tvDocStatus.setTextColor(Color.parseColor("#00796B"))
                tvDocStatus.setBackgroundColor(Color.parseColor("#E0F2F1"))
                ivDocAction.setImageResource(android.R.drawable.stat_sys_download)
                ivDocAction.setColorFilter(Color.parseColor("#00796B"))
            } else {
                tvDocStatus.setTextColor(Color.parseColor("#E65100"))
                tvDocStatus.setBackgroundColor(Color.parseColor("#FFF3E0"))
                ivDocAction.setImageResource(android.R.drawable.ic_menu_upload)
                ivDocAction.setColorFilter(Color.parseColor("#E65100"))
            }
        }
    }
}