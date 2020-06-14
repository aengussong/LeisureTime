package com.aengussong.leisuretime.ui

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.adapter.LeisureViewHolder
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.util.Tree
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.view.AndroidTreeView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseDataActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.leisureLiveData.observe(this, Observer {
            displayTree(it)
        })

        fab.setOnClickListener { showDialog() }
    }

    private val onNodeClickListener =
        TreeNode.TreeNodeClickListener { _, value -> viewModel.incrementCounter((value as Leisure).id) }

    private val onNodeLongCLickListener =
        TreeNode.TreeNodeLongClickListener { _, value ->
            startActivity(NodeDetailsActivity.getIntent(this, (value as Leisure).id))
            true
        }

    private val onAddSubNodeClickListener = { parentId: Long -> showDialog(parentId) }

    private fun displayTree(list: List<Tree<Leisure>>) {
        container.removeAllViews()

        val root = TreeNode.root()

        fun addNode(parent: TreeNode, list: List<Tree<Leisure>>) {
            list.forEach { tree ->
                val childNode = TreeNode(tree.value)
                    .setViewHolder(LeisureViewHolder(this, container, onAddSubNodeClickListener))
                    .setExpanded(true)
                parent.addChild(childNode)
                addNode(childNode, tree.children())
            }
        }

        addNode(root, list)

        val tView = AndroidTreeView(this, root).apply {
            setDefaultAnimation(true)
            setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
            setDefaultNodeClickListener(onNodeClickListener)
            setDefaultNodeLongClickListener(onNodeLongCLickListener)
        }
        container.addView(tView.view)
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
