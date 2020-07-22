package com.aengussong.leisuretime.util

import java.util.*
import java.util.regex.Pattern

/**
 * Main class to handle ancestry transformations. Only it should be used to modify ancestry in any
 * way. Manual ancestry manipulations are strongly discouraged.
 *
 * [ancestry] should be string, that starts with [ROOT_ANCESTRY] followed by element ids
 * with [DELIMITER]
 *
 * @throws IllFormattedAncestryException if [ancestry] does not start with [ROOT_ANCESTRY]
 * */
class AncestryBuilder(private var ancestry: String = ROOT_ANCESTRY) {

    init {
        /**
         * [ancestry] should always start with [ROOT_ANCESTRY] and end with [DELIMITER].
         * Elements ids always should be digits [0-9] separated by [DELIMITER]
         * */
        val matchesPattern = Pattern.matches("^$ROOT_ANCESTRY(\\d+$DELIMITER)*\$", ancestry)
        if (!matchesPattern)
            throw IllFormattedAncestryException()
    }

    override fun toString() = ancestry

    /**
     * Check whether current ancestry belongs to root element
     * */
    fun isRoot() = ancestry == ROOT_ANCESTRY

    /**
     * Returns null if leisure is root entity. [isRoot] should return true in this case
     *
     * @return id for root element, which holds this entity as sub element
     * */
    fun getRootParent() = getAncestryIds().firstOrNull()?.toLong()

    /**
     * Returns ancestry in stack format with parent ids on the top
     * */
    fun getAncestryStack() =
        Stack<Long>().apply { getAncestryIds().asReversed().forEach { push(it) } }

    /**
     * Throws [CyclingReferenceException] if cycling reference was detected in ancestry. Normally,
     * it should be handled, error shown to user and some event send to bug tracker, but right now
     * with all other checks implemented this exception shouldn't be thrown at all. Consider to handle
     * this situation in future.
     * */
    fun withChild(childId: Long): AncestryBuilder {
        val child = "$childId$DELIMITER"
        if (ancestry.contains(child)) throw CyclingReferenceException()
        ancestry += child
        return this
    }

    fun withChildren(children: List<Long>): AncestryBuilder {
        children.forEach { withChild(it) }
        return this
    }

    fun getAncestryIds() = ancestry.split(DELIMITER)
        .drop(1)
        .dropLast(1)
        .map { it.toLong() }
}

/**
 * Should be thrown when cycling reference on tree is detected
 * */
class CyclingReferenceException(msg: String = "Entity can't contain cycling reference in ancestry tree") :
    Exception(msg)

/**
 * Should be thrown when malformed ancestry is detected
 * */
class IllFormattedAncestryException(msg: String = "Ancestry should be started on root element") :
    IllegalArgumentException(msg)