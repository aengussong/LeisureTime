package com.aengussong.leisuretime.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import androidx.recyclerview.widget.DiffUtil
import com.aengussong.leisuretime.R


class LeisureAdapter : RecyclerView.Adapter<LeisureAdapter.LeisureViewHolder>() {

    var onClickListener:View.OnClickListener? = null

    var list = listOf<LeisureEntity>()
    private val diffCallback = LeisureDiffCallback()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeisureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leisure, parent, false)
        return LeisureViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LeisureViewHolder, position: Int) {
        val item = list[position]
        holder.itemView.setOnClickListener(onClickListener)
        holder.bind(item)
    }

    fun updateList(newList:List<LeisureEntity>){
        diffCallback.updateLists(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class LeisureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.name)

        fun bind(model: LeisureEntity) {
            nameTextView.text = model.name
        }
    }
}