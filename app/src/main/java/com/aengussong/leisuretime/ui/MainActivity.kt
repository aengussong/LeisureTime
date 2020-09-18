package com.aengussong.leisuretime.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.adapter.LeisureBinder
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.util.Tree
import com.aengussong.leisuretime.util.extention.doWhileActive
import kotlinx.android.synthetic.main.activity_main.*
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewAdapter

class MainActivity : BaseDataActivity() {

    private lateinit var adapter: TreeViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()
        fab.setOnClickListener { showAddLeisureDialog() }

        viewModel.leisureLiveData.observe(this, Observer {
            displayTree(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_sort) {
            viewModel.toggleSort()
            return true
        }
        return false
    }

    private fun onNodeLongCLick(id: Long) =
        startActivity(NodeDetailsActivity.getIntent(this, id))

    private fun onAddSubNodeClick(parentId: Long) =
        showAddLeisureDialog(parentId)

    private fun displayTree(list: List<Tree<Leisure>>) {
        val nodes = arrayListOf<TreeNode<*>>()

        fun addNode(parent: TreeNode<*>, list: List<Tree<Leisure>>) {
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
        val lmState = rv.layoutManager?.onSaveInstanceState()
        adapter.refresh(nodes)
        rv.layoutManager?.onRestoreInstanceState(lmState)
    }

    private fun setUpRecyclerView() {
        val nodes = arrayListOf<TreeNode<*>>()
        val binder = LeisureBinder()
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
                    viewModel.incrementCounter((node?.content as Leisure).id)
                    return true
                }

                override fun onToggle(p0: Boolean, p1: RecyclerView.ViewHolder?) {
                    TODO("Not yet implemented")
                }
            })

            setStableItemIdProvider { node: TreeNode<*>? -> (node?.content as Leisure).id }

            rv.adapter = this
        }
    }

    private fun showAddLeisureDialog(parentId: Long? = null) {
        val input = EditText(this).apply { setSingleLine() }
        AlertDialog.Builder(this)
            .setTitle(R.string.add_leisure)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.addLeisure(
                    input.text.toString(),
                    parentId
                )
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

}
