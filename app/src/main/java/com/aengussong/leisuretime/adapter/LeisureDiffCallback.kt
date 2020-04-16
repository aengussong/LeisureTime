package com.aengussong.leisuretime.adapter

import androidx.recyclerview.widget.DiffUtil
import com.aengussong.leisuretime.data.local.entity.LeisureEntity

class LeisureDiffCallback:DiffUtil.Callback(){

    private var oldList = listOf<LeisureEntity>()
    private var newList = listOf<LeisureEntity>()

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.name == newItem.name
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.name == newItem.name && oldItem.counter == newItem.counter && oldItem.updated == newItem.updated
    }

    fun updateLists(oldList:List<LeisureEntity>, newList:List<LeisureEntity>){
        this.oldList = oldList
        this.newList = newList
    }
}