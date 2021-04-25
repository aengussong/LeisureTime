package com.aengussong.prioritytime.adapter

import android.view.View
import android.widget.TextView
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.model.Task
import com.aengussong.prioritytime.util.extention.sendAsync
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

    override fun provideViewHolder(itemView: View?) = TaskViewHolder(itemView)

    inner class TaskViewHolder(
        private val view: View?, var content: Task? = null
    ) : TreeViewBinder.ViewHolder(view) {

        init {
            findViewById<View>(R.id.task_add).setOnClickListener {
                view?.sendAsync(itemClickChannel, content?.id)
            }
            view?.setOnLongClickListener { _ ->
                view.sendAsync(itemLongClickChannel, content?.id)
                true
            }
        }

        fun bind(content: Task) {
            this.content = content
            findViewById<TextView>(R.id.task_name).text = content.name
        }
    }
}