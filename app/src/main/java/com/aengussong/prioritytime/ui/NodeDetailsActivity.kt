package com.aengussong.prioritytime.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.util.extention.hide
import com.aengussong.prioritytime.util.extention.setOnEditorActionListener
import com.aengussong.prioritytime.util.extention.show
import kotlinx.android.synthetic.main.activity_node_details.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

private const val KEY_TASK_ID = "key_task"

class NodeDetailsActivity : BaseDataActivity() {

    private val formatter = SimpleDateFormat("dd MMMM", Locale.getDefault())

    companion object {
        fun getIntent(context: Context, taskId: Long) =
            Intent(context, NodeDetailsActivity::class.java).apply {
                putExtra(KEY_TASK_ID, taskId)
            }
    }

    private val taskId: Long by lazy { intent.getLongExtra(KEY_TASK_ID, -1L) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_details)

        viewModel.observeTask(taskId).filterNotNull().onEach { task: TaskEntity ->
            node_name.text = task.name
            node_counter.text = resources.getString(R.string.counter_times, task.counter)
            node_updated.text =
                resources.getString(R.string.last_updated, formatter.format(task.updated))
        }.launchIn(lifecycleScope)

        delete.setOnClickListener {
            viewModel.removeEntity(taskId)
            onBackPressed()
        }

        decrement.setOnClickListener { viewModel.decrementTask(taskId) }

        node_name.setOnClickListener {
            setTitleEditing(true)
        }

        node_name_et.setOnEditorActionListener(EditorInfo.IME_ACTION_DONE) {
            setTitleEditing(false)
            if (!it.text.isNullOrEmpty()) {
                viewModel.renameTask(taskId, it.text.toString())
            }
        }
    }

    private fun setTitleEditing(isEditing: Boolean) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (isEditing) {
            val nodeTitle = node_name.text.toString()
            node_name_et.setText(nodeTitle)
            node_name_et.show()
            node_name_et.requestFocus()
            node_name.hide()

            //show keyboard
            node_name_et.isFocusableInTouchMode = true
            imm.showSoftInput(node_name_et, 0)
        } else {
            node_name_et.hide()
            node_name.show()

            //hide keyboard
            imm.hideSoftInputFromWindow(node_name_et.windowToken, 0)
        }
    }
}
