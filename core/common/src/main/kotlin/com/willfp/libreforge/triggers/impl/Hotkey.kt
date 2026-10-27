package com.willfp.libreforge.triggers.impl

/**
 * A key listened for by [TriggerHotkey].
 */
enum class Hotkey(
    val id: String,
    val hasEvent: Boolean = true
) {
    SWAP_HANDS("swap_hands"),
    SLOT_1("1"),
    SLOT_2("2"),
    SLOT_3("3"),
    SLOT_4("4"),
    SLOT_5("5"),
    SLOT_6("6"),
    SLOT_7("7"),
    SLOT_8("8"),
    SLOT_9("9"),
    DROP("drop"),
    ADVANCEMENTS("advancements", hasEvent = false);

    companion object {
        private val slots = listOf(SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9)

        /**
         * The hotbar slot at [index], from 0 to 8.
         */
        fun slot(index: Int): Hotkey? = slots.getOrNull(index)
    }
}
