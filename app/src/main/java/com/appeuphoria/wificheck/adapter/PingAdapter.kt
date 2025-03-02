package com.appeuphoria.wificheck.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appeuphoria.wificheck.R

class PingAdapter(private val logList: MutableList<String>) : RecyclerView.Adapter<PingAdapter.LogViewHolder>() {

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logTextView: TextView = itemView.findViewById(R.id.logTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ping, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.logTextView.text = logList[position]
    }

    override fun getItemCount(): Int {
        return logList.size
    }
}
