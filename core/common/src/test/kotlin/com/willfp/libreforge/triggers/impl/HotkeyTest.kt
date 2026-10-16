package com.willfp.libreforge.triggers.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class HotkeyTest {
    @Test
    fun slotIndexesMapToNumberKeys() {
        for (index in 0..8) {
            assertEquals((index + 1).toString(), Hotkey.slot(index)?.id)
        }
    }

    @Test
    fun slotOutsideHotbarIsNull() {
        assertNull(Hotkey.slot(-1))
        assertNull(Hotkey.slot(9))
    }

    @Test
    fun idsAreUnique() {
        assertEquals(Hotkey.entries.size, Hotkey.entries.map { it.id }.toSet().size)
    }

    @Test
    fun onlyAdvancementsHasNoEvent() {
        assertEquals(listOf(Hotkey.ADVANCEMENTS), Hotkey.entries.filterNot { it.hasEvent })
        assertFalse(Hotkey.ADVANCEMENTS.hasEvent)
    }
}
