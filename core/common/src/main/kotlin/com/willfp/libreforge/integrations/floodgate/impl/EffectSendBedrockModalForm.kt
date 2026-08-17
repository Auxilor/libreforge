package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.floodgate.BedrockFormEvent
import com.willfp.libreforge.integrations.floodgate.BedrockFormType
import com.willfp.libreforge.integrations.floodgate.floodgate
import com.willfp.libreforge.integrations.floodgate.handleFormResult
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.geysermc.cumulus.form.ModalForm

object EffectSendBedrockModalForm : Effect<NoCompileData>("send_bedrock_modal_form") {
    override val description = "Shows the player a native Bedrock yes/no dialog, running commands for whichever button they pick."

    override val categories = setOf("chat", "player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Does nothing for Java players, who have no way to display a Bedrock form.",
        "Clicking a button runs that button's commands, then fires the bedrock_form_response trigger with value 0 for the first button and 1 for the second.",
        "Closing the dialog fires the bedrock_form_closed trigger instead, and runs nothing.",
        "Set form_id and filter on it with bedrock_form_id so your chains can tell forms apart."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "title",
            "You must specify the form title!",
            description = "The title shown at the top of the dialog.",
            type = ArgType.STRING,
            example = "&cConfirm"
        )
        optional(
            "content",
            description = "The body text shown above the two buttons.",
            type = ArgType.STRING,
            default = "",
            example = "Are you sure?"
        )
        optional(
            "form_id",
            description = "An ID for this form, matched by the bedrock_form_id filter on the response triggers.",
            type = ArgType.STRING,
            default = "",
            example = "confirm_purchase"
        )
        require(
            "button1",
            "You must specify the first button!",
            description = "The label of the first button.",
            type = ArgType.STRING,
            example = "Yes"
        )
        require(
            "button2",
            "You must specify the second button!",
            description = "The label of the second button.",
            type = ArgType.STRING,
            example = "No"
        )
        optional(
            listOf("button1_commands", "button1_command"),
            description = "The commands run as console when the first button is clicked.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("give %player% diamond 1")
        )
        optional(
            listOf("button2_commands", "button2_command"),
            description = "The commands run as console when the second button is clicked.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("say %player% declined")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (!isBedrockPlayer(player)) {
            return false
        }

        val context = config.toPlaceholderContext(data)

        val formId = config.getString("form_id")

        val firstCommands = config.getStrings("button1_commands", "button1_command")
        val secondCommands = config.getStrings("button2_commands", "button2_command")

        val firstText = config.getString("button1").translatePlaceholders(context)
        val secondText = config.getString("button2").translatePlaceholders(context)

        val form = ModalForm.builder()
            .title(config.getString("title").translatePlaceholders(context))
            .content(config.getString("content").translatePlaceholders(context))
            .button1(firstText)
            .button2(secondText)
            .validResultHandler { response ->
                val first = response.clickedFirst()

                handleFormResult(
                    BedrockFormEvent(
                        formId = formId,
                        formType = BedrockFormType.MODAL,
                        player = player,
                        isClosed = false,
                        buttonId = if (first) 0 else 1,
                        buttonText = if (first) firstText else secondText
                    ),
                    if (first) firstCommands else secondCommands,
                    config,
                    data
                )
            }
            .closedOrInvalidResultHandler { ->
                handleFormResult(
                    BedrockFormEvent(
                        formId = formId,
                        formType = BedrockFormType.MODAL,
                        player = player,
                        isClosed = true
                    ),
                    emptyList(),
                    config,
                    data
                )
            }
            .build()

        return floodgate.sendForm(player.uniqueId, form)
    }
}
