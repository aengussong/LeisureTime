package com.aengussong.leisuretime.util

class Tree<T>(val value: T) {
    private var children = mutableListOf<Tree<T>>()

    fun childrenCount() = children.size
    fun addSubtree(subtree: Tree<T>) = children.add(subtree)
    fun levels(): Int {
        var levels = 0
        children.forEach { levels = levels.coerceAtLeast(it.levels()) }
        return levels + 1
    }

    fun sortChildren(comparator: Comparator<Tree<T>>) {
        val sorted = children.sortedWith(comparator)
        children = sorted.toMutableList()
        children.forEach { subTree -> subTree.sortChildren(comparator) }
    }

    fun children(): List<Tree<T>> = children
}