package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerBedrockFormResponse : Trigger("bedrock_form_response") {
    override val description = "Fires when a player answers a Bedrock form that libreforge sent them."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Only fires for forms sent by send_bedrock_form, send_bedrock_modal_form, or send_bedrock_custom_form.",
        "Use the bedrock_form_id filter to tell your forms apart; without it every form feeds every chain.",
        "Does not fire when the player closes the form; use bedrock_form_closed for that."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The text of the clicked button, or the first answer for a custom form.",
        TriggerParameter.VALUE to "The index of the clicked button, starting at 0, or -1 for a custom form.",
        TriggerParameter.LOCATION to "The player's location when they answered."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.VALUE,
        TriggerParameter.EVENT
    )
}
