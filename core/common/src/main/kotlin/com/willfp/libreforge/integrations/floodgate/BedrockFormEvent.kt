package com.willfp.libreforge.integrations.floodgate

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * The kind of Bedrock form a [BedrockFormEvent] came from.
 */
enum class BedrockFormType {
    /** A list of buttons. */
    SIMPLE,

    /** A two button dialog. */
    MODAL,

    /** A form of inputs, toggles, sliders, and dropdowns. */
    CUSTOM
}

/**
 * Fired when a player finishes with a Bedrock form that libreforge sent them, either by
 * answering it or by closing it.
 *
 * Always fired on the main thread, even though Cumulus hands the response back on a netty
 * thread.
 *
 * @param formId The form_id given to the effect that sent the form, or an empty string.
 * @param formType The kind of form this came from.
 * @param player The player who was shown the form.
 * @param isClosed If the player dismissed the form rather than answering it.
 * @param buttonId The index of the clicked button, or -1 for a closed or custom form.
 * @param buttonText The text of the clicked button, or null for a closed or custom form.
 * @param inputs The answers to a custom form's components, in order, as strings. Labels are
 * skipped; toggles are "true" or "false"; dropdowns and step sliders give the chosen option
 * text rather than its index.
 */
class BedrockFormEvent(
    val formId: String,
    val formType: BedrockFormType,
    val player: Player,
    val isClosed: Boolean,
    val buttonId: Int = -1,
    val buttonText: String? = null,
    val inputs: List<String> = emptyList()
) : Event() {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
