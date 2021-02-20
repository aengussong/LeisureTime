package com.aengussong.prioritytime.adapter

import android.view.View
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.model.Task
import com.aengussong.prioritytime.util.extention.sendAsync
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewBinder

class TaskBinder : TreeViewBinder<TaskBinder.TaskViewHolder>() {

    private val itemClickChannel = Channel<Long>()
    private val itemLongClickChannel = Channel<Long>()

    fun getItemClickChannel(): ReceiveChannel<Long> = itemClickChannel

    fun getItemLongClickChannel(): ReceiveChannel<Long> = itemLongClickChannel

    override fun bindView(holder: TaskViewHolder?, position: Int, node: TreeNode<*>?) {
        val content = node?.content as Task
        holder?.bind(content)
    }

    override fun getLayoutId() = R.layout.item_task

    override fun provideViewHolder(itemView: View?) = TaskViewHolder(itemView).apply {
        itemView?.let { view ->
            view.task_add?.setOnClickListener { _ ->
                view.sendAsync(itemClickChannel, content?.id)
            }
            view.setOnLongClickListener { _ ->
                view.sendAsync(itemLongClickChannel, content?.id)
                true
            }
        }
    }

    class TaskViewHolder(
        private val view: View?, var content: Task? = null
    ) : TreeViewBinder.ViewHolder(view) {

        fun bind(content: Task) {
            this.view?.task_name?.text = content.name
            this.content = content
        }
    }
}