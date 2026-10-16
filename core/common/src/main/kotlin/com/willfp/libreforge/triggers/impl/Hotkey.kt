package com.willfp.libreforge.triggers.impl

private const val SLOT_NOTE = "Also fires when scrolling onto the slot, as the server cannot tell " +
        "a number key apart from the scroll wheel."

/**
 * A key listened for by [TriggerHotkey].
 */
enum class Hotkey(
    val id: String,
    val action: String,
    val itemDescription: String,
    val hasEvent: Boolean = true,
    val note: String? = null
) {
    SWAP_HANDS(
        "swap_hands",
        "presses the swap hands key (F by default)",
        "The item in the player's main hand."
    ),
    SLOT_1("1", "presses hotbar key 1", "The item in hotbar slot 1.", note = SLOT_NOTE),
    SLOT_2("2", "presses hotbar key 2", "The item in hotbar slot 2.", note = SLOT_NOTE),
    SLOT_3("3", "presses hotbar key 3", "The item in hotbar slot 3.", note = SLOT_NOTE),
    SLOT_4("4", "presses hotbar key 4", "The item in hotbar slot 4.", note = SLOT_NOTE),
    SLOT_5("5", "presses hotbar key 5", "The item in hotbar slot 5.", note = SLOT_NOTE),
    SLOT_6("6", "presses hotbar key 6", "The item in hotbar slot 6.", note = SLOT_NOTE),
    SLOT_7("7", "presses hotbar key 7", "The item in hotbar slot 7.", note = SLOT_NOTE),
    SLOT_8("8", "presses hotbar key 8", "The item in hotbar slot 8.", note = SLOT_NOTE),
    SLOT_9("9", "presses hotbar key 9", "The item in hotbar slot 9.", note = SLOT_NOTE),
    DROP(
        "drop",
        "presses the drop key (Q by default) outside of an inventory",
        "The item that was dropped.",
        note = "Drops made by clicking in an inventory are ignored. This is detected on a best-effort basis."
    ),
    ADVANCEMENTS(
        "advancements",
        "opens the advancements screen (L by default)",
        "The item in the player's main hand.",
        hasEvent = false,
        note = "Detected from the packet the client sends when the screen opens, so a modified client can skip it. " +
                "Switching tabs while the screen is open does not fire it again."
    );

    companion object {
        private val slots = listOf(SLOT_1, SLOT_2, SLOT_3, SLOT_4, SLOT_5, SLOT_6, SLOT_7, SLOT_8, SLOT_9)

        /**
         * The hotkey for the hotbar slot at [index], from 0 to 8.
         */
        fun slot(index: Int): Hotkey? = slots.getOrNull(index)
    }
}
