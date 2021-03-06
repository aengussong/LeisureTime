package com.aengussong.prioritytime.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.databinding.ActivityNodeDetailsBinding
import com.aengussong.prioritytime.util.extention.hide
import com.aengussong.prioritytime.util.extention.setOnEditorActionListener
import com.aengussong.prioritytime.util.extention.show
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

private const val KEY_TASK_ID = "key_task"

class NodeDetailsActivity : BaseDataActivity() {

    private val formatter = SimpleDateFormat("dd MMMM", Locale.getDefault())

    private lateinit var binding: ActivityNodeDetailsBinding

    companion object {
        fun getIntent(context: Context, taskId: Long) =
            Intent(context, NodeDetailsActivity::class.java).apply {
                putExtra(KEY_TASK_ID, taskId)
            }
    }

    private val taskId: Long by lazy { intent.getLongExtra(KEY_TASK_ID, -1L) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNodeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeTask(taskId).filterNotNull().onEach { task: TaskEntity ->
            binding.nodeName.text = task.name
            binding.nodeCounter.text = resources.getString(R.string.counter_times, task.counter)
            binding.nodeUpdated.text =
                resources.getString(R.string.last_updated, formatter.format(task.updated))
        }.launchIn(lifecycleScope)

        binding.delete.setOnClickListener {
            showDeleteDialog()
        }

        binding.decrement.setOnClickListener { viewModel.decrementTask(taskId) }

        binding.nodeName.setOnClickListener {
            setTitleEditing(true)
        }

        binding.nodeNameEt.setOnEditorActionListener(EditorInfo.IME_ACTION_DONE) {
            setTitleEditing(false)
            if (!it.text.isNullOrEmpty()) {
                viewModel.renameTask(taskId, it.text.toString())
            }
        }
    }

    private fun setTitleEditing(isEditing: Boolean) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (isEditing) {
            val nodeTitle = binding.nodeName.text.toString()
            binding.nodeNameEt.setText(nodeTitle)
            binding.nodeNameEt.show()
            binding.nodeNameEt.requestFocus()
            binding.nodeName.hide()

            //show keyboard
            binding.nodeNameEt.isFocusableInTouchMode = true
            imm.showSoftInput(binding.nodeNameEt, 0)
        } else {
            binding.nodeNameEt.hide()
            binding.nodeName.show()

            //hide keyboard
            imm.hideSoftInputFromWindow(binding.nodeNameEt.windowToken, 0)
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_remove_item)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.removeEntity(taskId)
                onBackPressed()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }
}
