package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerBedrockFormClosed : Trigger("bedrock_form_closed") {
    override val description = "Fires when a player dismisses a Bedrock form that libreforge sent them without answering it."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Only fires for forms sent by send_bedrock_form, send_bedrock_modal_form, or send_bedrock_custom_form.",
        "Use the bedrock_form_id filter to tell your forms apart; without it every form feeds every chain.",
        "Also fires if the client sends a response libreforge cannot read, so treat it as 'no answer' rather than strictly 'closed'."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location when they closed the form."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
}
