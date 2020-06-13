package com.aengussong.leisuretime.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.model.Leisure
import com.unnamed.b.atv.model.TreeNode
import kotlinx.android.synthetic.main.item_leisure.view.*


class LeisureViewHolder(
    context: Context,
    private val vg: ViewGroup,
    private val onAddClickListener: (Long) -> Unit
) :
    TreeNode.BaseNodeViewHolder<Leisure>(context) {

    override fun createNodeView(node: TreeNode, value: Leisure): View =
        LayoutInflater.from(context).inflate(R.layout.item_leisure, vg, false).apply {
            this.leisure_name.text = value.name
            this.leisure_counter.text = value.counter.toString()
            this.leisure_add.setOnClickListener { onAddClickListener(value.id) }
        }

}