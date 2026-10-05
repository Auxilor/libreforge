package com.willfp.libreforge.holders

import com.willfp.libreforge.KeyChanges
import com.willfp.libreforge.isSameAnswer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HolderChangesTest {
    private data class Provided(val holder: String, val slot: String? = null)

    private fun answer(vararg entries: Pair<String, Provided>) = linkedMapOf(*entries)

    private fun classify(old: Map<String, Provided>, new: Map<String, Provided>) =
        KeyChanges<String, Provided>().apply { classify(old, new, { it.holder }, { it.slot }) }

    @Test
    fun newKeysAreAdded() {
        val changes = classify(answer(), answer("a" to Provided("sword")))

        assertEquals(listOf("a" to Provided("sword")), changes.added)
        assertEquals(emptyList<Pair<String, Provided>>(), changes.removed)
        assertEquals(emptySet<String>(), changes.moved)
    }

    @Test
    fun missingKeysAreRemoved() {
        val changes = classify(answer("a" to Provided("sword")), answer())

        assertEquals(emptyList<Pair<String, Provided>>(), changes.added)
        assertEquals(listOf("a" to Provided("sword")), changes.removed)
    }

    @Test
    fun slotChangeIsAMove() {
        val changes = classify(
            answer("a" to Provided("sword", "mainhand")),
            answer("a" to Provided("sword", "offhand"))
        )

        assertEquals(setOf("a"), changes.moved)
        assertTrue(changes.added.isEmpty() && changes.removed.isEmpty(), "A move neither adds nor removes")
    }

    @Test
    fun holderSwapUnderSameKeyIsRemoveThenAdd() {
        val changes = classify(
            answer("a" to Provided("sword", "mainhand")),
            answer("a" to Provided("axe", "offhand"))
        )

        assertEquals(listOf("a" to Provided("sword", "mainhand")), changes.removed)
        assertEquals(listOf("a" to Provided("axe", "offhand")), changes.added)
        assertEquals(emptySet<String>(), changes.moved, "A swap is not also a move")
    }

    @Test
    fun unchangedKeysAreIgnored() {
        val same = answer("a" to Provided("sword", "mainhand"))
        val changes = classify(same, LinkedHashMap(same))

        assertTrue(changes.added.isEmpty() && changes.removed.isEmpty() && changes.moved.isEmpty())
    }

    @Test
    fun classifyingSeveralProvidersAccumulates() {
        val changes = KeyChanges<String, Provided>()
        changes.classify(answer(), answer("a" to Provided("sword")), { it.holder }, { it.slot })
        changes.classify(answer("b" to Provided("ring")), answer(), { it.holder }, { it.slot })

        assertEquals(listOf("a" to Provided("sword")), changes.added)
        assertEquals(listOf("b" to Provided("ring")), changes.removed)
    }

    @Test
    fun sameAnswerNeedsSameKeysValuesAndOrder() {
        val old = answer("a" to Provided("sword"), "b" to Provided("ring"))

        assertTrue(isSameAnswer(old, answer("a" to Provided("sword"), "b" to Provided("ring"))) { it.slot })
        assertFalse(isSameAnswer(old, answer("b" to Provided("ring"), "a" to Provided("sword"))) { it.slot }, "Order matters")
        assertFalse(isSameAnswer(old, answer("a" to Provided("sword"))) { it.slot }, "Size matters")
        assertFalse(isSameAnswer(old, answer("a" to Provided("axe"), "b" to Provided("ring"))) { it.slot })
    }

    @Test
    fun slotChangeIsADifferentAnswer() {
        val old = answer("a" to Provided("sword", "mainhand"))

        assertFalse(isSameAnswer(old, answer("a" to Provided("sword", "offhand"))) { it.slot })
    }
}
