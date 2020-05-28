package com.aengussong.leisuretime.util

import org.junit.Assert.*
import org.junit.Test

class AncestryBuilderTest {

    @Test(expected = IllFormattedAncestryException::class)
    fun `initialize builder with ancestry that does not start on root - should throw exception`() {
        AncestryBuilder("2/3/")
    }

    @Test(expected = IllFormattedAncestryException::class)
    fun `initialize builder with ancestry that does not end with delimiter - should throw exception`() {
        AncestryBuilder("2/3")
    }

    @Test(expected = IllFormattedAncestryException::class)
    fun `initialize builder with non ancestry characters - should throw exception`() {
        //allowed characters [0-9] and $DELIMITER, $ROOT_ANCESTRY is mandatory suffix
        AncestryBuilder("${ROOT_ANCESTRY}2/q/")
    }

    @Test(expected = IllFormattedAncestryException::class)
    fun `initialize builder with doubled delimiter - should throw exception`() {
        AncestryBuilder("${ROOT_ANCESTRY}2/4//5/")
    }

    @Test
    fun `create ancestry with child element - should have delimiter at the end`() {
        val builder = AncestryBuilder().addChild(2)

        val result = builder.toString()

        assertTrue(result.endsWith(DELIMITER))
    }

    @Test
    fun `check is root on root ancestry - should return true`() {
        val builder = AncestryBuilder()

        val result = builder.isRoot()

        assertTrue(result)
    }

    @Test
    fun `check is root on non root ancestry - should return false`() {
        val builder = AncestryBuilder().addChild(2)

        val result = builder.isRoot()

        assertFalse(result)
    }

    @Test(expected = CyclingReferenceException::class)
    fun `add cyclic reference - should throw exception`() {
        AncestryBuilder().addChild(2).addChild(2)
    }

    @Test
    fun `get root parent - should return valid root parent`() {
        val rootParent = 5L
        val builder = AncestryBuilder().addChild(rootParent).addChild(2)

        val result = builder.getRootParent()

        assertEquals(rootParent, result)
    }

    @Test
    fun `get root parent on root element - should return null`() {
        val builder = AncestryBuilder()

        val result = builder.getRootParent()

        assertNull(result)
    }

    @Test
    fun `on root element - root parent should be null`() {
        val builder = AncestryBuilder()

        assertTrue(builder.isRoot())
        assertNull(builder.getRootParent())
    }

    @Test
    fun `on non root element - root should return parent id`() {
        val parentId = 3L
        val builder = AncestryBuilder().addChild(parentId).addChild(4).addChild(2)

        assertFalse(builder.isRoot())
        assertEquals(parentId, builder.getRootParent())
    }

}