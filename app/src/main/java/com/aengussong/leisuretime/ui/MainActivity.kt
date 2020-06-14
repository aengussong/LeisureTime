package com.aengussong.leisuretime.ui

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.adapter.LeisureBinder
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.util.Tree
import kotlinx.android.synthetic.main.activity_main.*
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewAdapter

class MainActivity : BaseDataActivity() {

    private lateinit var adapter: TreeViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayRv()
        fab.setOnClickListener { showDialog() }

        viewModel.leisureLiveData.observe(this, Observer {
            displayTree(it)
        })
    }

    private val onNodeLongCLickListener = { id: Long ->
        startActivity(NodeDetailsActivity.getIntent(this, id))
    }

    private val onAddSubNodeClickListener = { parentId: Long -> showDialog(parentId) }

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

        adapter.refresh(nodes)
    }

    private fun displayRv() {
        val nodes = arrayListOf<TreeNode<*>>()
        adapter = TreeViewAdapter(
            nodes,
            listOf(LeisureBinder(onAddSubNodeClickListener, onNodeLongCLickListener))
        ).apply {
            setPadding(resources.getDimension(R.dimen.node_indent).toInt())

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

            rv.adapter = this
        }
    }

    private fun showDialog(parentId: Long? = null) {
        val input = EditText(this)
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
