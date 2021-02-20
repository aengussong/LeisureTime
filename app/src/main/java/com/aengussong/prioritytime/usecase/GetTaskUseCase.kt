package com.aengussong.prioritytime.usecase

import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.Task
import com.aengussong.prioritytime.usecase.mapper.Mapper
import com.aengussong.prioritytime.util.AncestryBuilder
import com.aengussong.prioritytime.util.Tree
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.Comparator

class GetTaskUseCase(private val repo: TaskRepository) : Mapper() {

    fun getHierarchialTasks(): Flow<List<Tree<Task>>> =
        repo.getHierarchialTasks().map { it.toTaskHierarchy() }

    fun getLinearTasks(): Flow<List<Tree<Task>>> {
        return repo.getLinearTasks().map { list ->
            list.map { Task(it.id, it.name, it.counter, it.updated) }
                .map { Tree(it) }
                .sortedWith(treeComparator)
        }
    }

    fun getTask(id: Long): Flow<TaskEntity?> = repo.observeTask(id)

    fun observeMinTask(): Flow<TaskEntity?> = repo.observeMinTask()

    suspend fun getMinTask(): TaskEntity? = repo.getMinTask()

    /**
     * Current db implementation returns entities ordered by ancestry, so all root elements are
     * positioned on top of the list, and all child elements with greatest nesting are at the
     * bottom. Error, occurred in this function points into wrong implementation of the db elements
     * retrieval, or flaws in cascade deleting (possible source of errors - item moving to another
     * parent, if implemented).
     **/
    private fun List<TaskEntity>.toTaskHierarchy(): List<Tree<Task>> {
        val trees = mutableMapOf<Long, Tree<Task>>()
        this.forEach { taskEntity ->
            val ancestry = AncestryBuilder(taskEntity.ancestry)
            if (ancestry.isRoot()) {
                val task = taskEntity.toTask()
                trees[taskEntity.id] = Tree(task)
            } else {
                val rootParent = ancestry.getRootParent()
                val ancestryStack = ancestry.getAncestryStack()
                trees[rootParent]?.addToSubtree(ancestryStack, taskEntity.toTask())
            }
        }

        return trees.values.toList().sort(treeComparator)
    }

    private fun Tree<Task>.addToSubtree(ancestry: Stack<Long>, task: Task) {
        val parentId = ancestry.pop()
        if (value.id == parentId && ancestry.isEmpty()) {
            addSubtree(Tree(task))
            return
        }
        val subParentId = ancestry.peek()
        val parentTree = children().firstOrNull { it.value.id == subParentId }
        parentTree?.addToSubtree(ancestry, task) ?: throw NullPointerException()
    }

    private fun List<Tree<Task>>.sort(comparator: Comparator<Tree<Task>>): List<Tree<Task>> {
        forEach { tree -> tree.sortChildren(comparator) }
        return sortedWith(comparator)
    }

    /**
     * Descending comparator by `counter` and `updated` fields. Counter considered to be the most
     * important comparable value, two items compared by date only if they have the same counter.
     * */
    private val treeComparator = Comparator<Tree<Task>> { tree1, tree2 ->
        val counterDiff = tree1.value.counter - tree2.value.counter
        return@Comparator if (counterDiff == 0L) {
            tree1.value.updated.time - tree2.value.updated.time
        } else {
            counterDiff
        }.toInt()
    }
}