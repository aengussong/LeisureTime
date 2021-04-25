package com.aengussong.prioritytime.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.adapter.TaskBinder
import com.aengussong.prioritytime.databinding.ActivityMainBinding
import com.aengussong.prioritytime.model.Task
import com.aengussong.prioritytime.util.Tree
import com.aengussong.prioritytime.util.extention.doWhileActive
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewAdapter

class MainActivity : BaseDataActivity() {

    private lateinit var adapter: TreeViewAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        binding.fab.setOnClickListener { showAddTaskDialog() }

        viewModel.taskLiveData.observe(this, Observer {
            displayTree(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_sort -> {
                viewModel.toggleSort()
                true
            }
            R.id.main_erase -> {
                showClearCountersDialog()
                true
            }
            else -> false
        }
    }

    private fun onNodeLongCLick(id: Long) =
        startActivity(NodeDetailsActivity.getIntent(this, id))

    private fun onAddSubNodeClick(parentId: Long) =
        showAddTaskDialog(parentId)

    private fun displayTree(list: List<Tree<Task>>) {
        val nodes = arrayListOf<TreeNode<*>>()

        fun addNode(parent: TreeNode<*>, list: List<Tree<Task>>) {
            list.forEach { tree ->
                val childNode = TreeNode(tree.value).apply { toggle() }
                parent.addChild(childNode)
                addNode(childNode, tree.children())
            }
        }
        list.forEach {
            val rootNode = TreeNode(it.value).apply { toggle() }
            addNode(rootNode, it.children())
            nodes.add(rootNode)
        }

        /*For some reason, if position of the first item in the RecyclerView is changed,
        layout manager thinks that user would be glad if this item will be still visible after
        item movement, so it autoscrolls to the new position of the first element. To prevent
        this behaviour saving state before changes and restoring it after was implemented,
        preventing ANY autoscrolling from happening. */
        val lmState = binding.rv.layoutManager?.onSaveInstanceState()
        adapter.refresh(nodes)
        binding.rv.layoutManager?.onRestoreInstanceState(lmState)
    }

    private fun setUpRecyclerView() {
        val nodes = arrayListOf<TreeNode<*>>()
        val binder = TaskBinder()
        lifecycleScope.doWhileActive {
            val clickedId = binder.getItemClickChannel().receive()
            onAddSubNodeClick(clickedId)
        }
        lifecycleScope.doWhileActive {
            val longClickedId = binder.getItemLongClickChannel().receive()
            onNodeLongCLick(longClickedId)
        }
        adapter = TreeViewAdapter(
            nodes,
            listOf(binder)
        ).apply {
            setPadding(resources.getDimension(R.dimen.node_indent).toInt())
            setHasStableIds(true)

            setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener {
                override fun onClick(
                    node: TreeNode<*>?,
                    holder: RecyclerView.ViewHolder?
                ): Boolean {
                    viewModel.incrementCounter((node?.content as Task).id)
                    return true
                }

                override fun onToggle(p0: Boolean, p1: RecyclerView.ViewHolder?) {
                    // noop
                }
            })

            setStableItemIdProvider { node: TreeNode<*>? -> (node?.content as Task).id }

            binding.rv.adapter = this
        }
    }

    private fun showAddTaskDialog(parentId: Long? = null) {
        val input = EditText(this).apply { setSingleLine() }
        AlertDialog.Builder(this)
            .setTitle(R.string.add_task)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.addTask(
                    input.text.toString(),
                    parentId
                )
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showClearCountersDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_clear_all_counters)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.dropCounters()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

}
