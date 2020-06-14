package com.aengussong.leisuretime.adapter

import android.view.View
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.model.Leisure
import kotlinx.android.synthetic.main.item_leisure.view.*
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewBinder

class LeisureBinder(
    private val onAddSub: (id: Long) -> Unit,
    private val onLongClick: (id: Long) -> Unit
) : TreeViewBinder<TreeViewBinder.ViewHolder>() {

    override fun bindView(holder: ViewHolder?, position: Int, node: TreeNode<*>?) {
        val content = node?.content as Leisure
        holder?.itemView?.let {
            it.leisure_counter?.text = content.counter.toString()
            it.leisure_name?.text = content.name
            it.leisure_add?.setOnClickListener { _ -> onAddSub(content.id) }
            it.setOnLongClickListener { _ -> onLongClick(content.id); true }
        }
    }

    override fun getLayoutId() = R.layout.item_leisure

    override fun provideViewHolder(itemView: View?) = ViewHolder(itemView)
}