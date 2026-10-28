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

        event.items

        assertEquals(3, event.items.single().xp)
    }
}
