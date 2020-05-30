package com.aengussong.leisuretime.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.usecase.mapper.Mapper
import com.aengussong.leisuretime.util.AncestryBuilder
import com.aengussong.leisuretime.util.Tree
import java.util.*
import kotlin.Comparator

class GetLeisureUseCase(private val repo: LeisureRepository) : Mapper() {

    /**
     * Descending comparator by counter and updated fields. Counter considered to be the most
     * important comparable value, two items compared by date only if they have same counter.
     * */
    private val comparator = Comparator<Tree<Leisure>> { tree1, tree2 ->
        val counterDiff = tree1.value.counter - tree2.value.counter
        val equality = if (counterDiff == 0L) {
            tree1.value.updated.time - tree2.value.updated.time
        } else counterDiff
        equality.toInt()
    }


    fun execute(): LiveData<List<Tree<Leisure>>> {
        return Transformations.map(repo.getLeisures()) { list: List<LeisureEntity> ->
            list.toLeisureHierarchy()
        }
    }

    /**
     * Current db implementation returns entities ordered by ancestry, so all root elements are
     * positioned on top of the list, and all child elements with greatest nesting are at the
     * bottom. Error, occurred in this function points into wrong implementation of the db elements
     * retrieval, or flaws in cascade deleting (possible source of errors - item moving to another
     * parent, if implemented).
     **/
    private fun List<LeisureEntity>.toLeisureHierarchy(): List<Tree<Leisure>> {
        val trees = mutableMapOf<Long, Tree<Leisure>>()
        this.forEach { leisureEntity ->
            val ancestry = AncestryBuilder(leisureEntity.ancestry)
            if (ancestry.isRoot()) {
                val leisure = leisureEntity.toLeisure()
                trees[leisureEntity.id] = Tree(leisure)
            } else {
                val rootParent = ancestry.getRootParent()
                val ancestryStack = ancestry.getAncestryStack()
                trees[rootParent]?.addToSubtree(ancestryStack, leisureEntity.toLeisure())
            }
        }

        return trees.values.toList().sort(comparator)
    }

    private fun Tree<Leisure>.addToSubtree(ancestry: Stack<Long>, leisure: Leisure) {
        val parentId = ancestry.pop()
        if (value.id == parentId && ancestry.isEmpty()) {
            addSubtree(Tree(leisure))
            return
        }
        val subParentId = ancestry.peek()
        val parentTree = children().firstOrNull { it.value.id == subParentId }
        parentTree?.addToSubtree(ancestry, leisure) ?: throw NullPointerException()
    }

    private fun List<Tree<Leisure>>.sort(comparator: Comparator<Tree<Leisure>>): List<Tree<Leisure>> {
        forEach { tree -> tree.sortChildren(comparator) }
        return sortedWith(comparator)
    }
}