package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.floodgate.BedrockFormEvent
import com.willfp.libreforge.triggers.TriggerData

object FilterBedrockFormId : Filter<NoCompileData, Collection<String>>("bedrock_form_id") {
    override val description = "Matches when the Bedrock form that was answered or closed has one of the given IDs."

    override val categories = setOf("player")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Only meaningful on the bedrock_form_response and bedrock_form_closed triggers; it does not pass on any other trigger.",
        "The ID is the form_id argument given to the effect that sent the form."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? BedrockFormEvent ?: return false

        return value.containsIgnoreCase(event.formId)
    }
}
