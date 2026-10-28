package com.willfp.libreforge.triggers.event

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EditableDropEventTest {
    private fun eventWith(vararg drops: ItemStack) =
        EditableDropEvent(drops.toList(), DropCause.BLOCK, DropContext(), Location(null, 0.0, 0.0, 0.0))

    @Test
    fun `modifiers run once per drop`() {
        var runs = 0
        val event = eventWith(object : ItemStack() {})
        event.addModifier { drop ->
            runs++
            DropResult(drop, 1)
        }

        event.items
        val second = event.items

        assertEquals(1, runs)
        assertEquals(1, second.single().xp)
    }

    @Test
    fun `later modifiers still run`() {
        val event = eventWith(object : ItemStack() {})
        event.addModifier { drop -> DropResult(drop, 1) }
        event.items

        event.addModifier { drop -> DropResult(drop, 2) }

        assertEquals(3, event.items.single().xp)
    }

    @Test
    fun `modifier added mid-read runs`() {
        val event = eventWith(object : ItemStack() {})
        event.addModifier { drop ->
            event.addModifier { inner -> DropResult(inner, 2) }
            DropResult(drop, 1)
        }

        assertEquals(3, event.items.single().xp)
    }

    @Test
    fun `equal drops each modify`() {
        val event = eventWith(object : ItemStack() {}, object : ItemStack() {})
        var runs = 0
        event.addModifier { drop ->
            runs++
            DropResult(drop, 1)
        }

        event.items

        assertEquals(2, runs)
        assertEquals(listOf(1, 1), event.items.map { it.xp })
    }

    @Test
    fun `mid-read modifier hits all`() {
        val event = eventWith(object : ItemStack() {}, object : ItemStack() {})
        event.addModifier { drop ->
            if (event.drops.first() === drop) {
                event.addModifier { inner -> DropResult(inner, 2) }
            }
            DropResult(drop, 1)
        }

        assertEquals(listOf(3, 3), event.items.map { it.xp })
    }
}
