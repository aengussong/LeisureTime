package com.aengussong.leisuretime.adapter

import android.view.View
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.util.extention.sendAsync
import kotlinx.android.synthetic.main.item_leisure.view.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewBinder

class LeisureBinder : TreeViewBinder<LeisureBinder.LeisureViewHolder>() {

    private val itemClickChannel = Channel<Long>()
    private val itemLongClickChannel = Channel<Long>()

    fun getItemClickChannel(): ReceiveChannel<Long> = itemClickChannel

    fun getItemLongClickChannel(): ReceiveChannel<Long> = itemLongClickChannel

    override fun bindView(holder: LeisureViewHolder?, position: Int, node: TreeNode<*>?) {
        val content = node?.content as Leisure
        holder?.bind(content)
    }

    override fun getLayoutId() = R.layout.item_leisure

    override fun provideViewHolder(itemView: View?) = LeisureViewHolder(itemView).apply {
        itemView?.let { view ->
            view.leisure_add?.setOnClickListener { _ ->
                view.sendAsync(itemClickChannel, content?.id)
            }
            view.setOnLongClickListener { _ ->
                view.sendAsync(itemLongClickChannel, content?.id)
                true
            }
        }
    }

    class LeisureViewHolder(
        private val view: View?, var content: Leisure? = null
    ) : TreeViewBinder.ViewHolder(view) {

        fun bind(content: Leisure) {
            this.view?.leisure_name?.text = content.name
            this.content = content
        }
    }
}