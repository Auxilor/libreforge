package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.floodgate.BedrockFormEvent
import com.willfp.libreforge.integrations.floodgate.BedrockFormType
import com.willfp.libreforge.integrations.floodgate.floodgate
import com.willfp.libreforge.integrations.floodgate.handleFormResult
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.util.FormImage

/**
 * A button on a [EffectSendBedrockForm], as parsed from config.
 */
data class BedrockFormButton(
    val text: String,
    val commands: List<String>,
    val image: FormImage?
)

object EffectSendBedrockForm : Effect<List<BedrockFormButton>>("send_bedrock_form") {
    override val description = "Shows the player a native Bedrock form with a list of buttons."

    override val categories = setOf("chat", "player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Does nothing for Java players, who have no way to display a Bedrock form.",
        "Clicking a button runs that button's commands, then fires the bedrock_form_response trigger.",
        "Closing the form fires the bedrock_form_closed trigger instead, and runs nothing.",
        "Set form_id and filter on it with bedrock_form_id so your chains can tell forms apart."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "title",
            "You must specify the form title!",
            description = "The title shown at the top of the form.",
            type = ArgType.STRING,
            example = "&aServer Menu"
        )
        optional(
            "content",
            description = "The description shown under the title, above the buttons.",
            type = ArgType.STRING,
            default = "",
            example = "Pick an option"
        )
        optional(
            "form_id",
            description = "An ID for this form, matched by the bedrock_form_id filter on the response triggers.",
            type = ArgType.STRING,
            default = "",
            example = "shop_categories"
        )
        require(
            "buttons",
            "You must specify the buttons!",
            description = "The buttons on the form, in order, each with the commands it runs.",
            type = ArgType.DYNAMIC,
            schema = BedrockFormButtonSpec::class
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: List<BedrockFormButton>): Boolean {
        val player = data.player ?: return false

        if (!isBedrockPlayer(player) || compileData.isEmpty()) {
            return false
        }

        val context = config.toPlaceholderContext(data)
        val formId = config.getString("form_id")

        val builder = SimpleForm.builder()
            .title(config.getString("title").translatePlaceholders(context))
            .content(config.getString("content").translatePlaceholders(context))

        for (button in compileData) {
            builder.button(button.text.translatePlaceholders(context), button.image)
        }

        /*
        Buttons are matched by index rather than by attaching a callback to each one, because
        an index outside the list means the response is not for a form we built, and that is
        worth treating as no answer rather than silently running the wrong button.
         */
        builder.validResultHandler { response ->
            val id = response.clickedButtonId()
            val clicked = compileData.getOrNull(id)

            handleFormResult(
                BedrockFormEvent(
                    formId = formId,
                    formType = BedrockFormType.SIMPLE,
                    player = player,
                    isClosed = clicked == null,
                    buttonId = if (clicked == null) -1 else id,
                    buttonText = clicked?.text
                ),
                clicked?.commands ?: emptyList(),
                config,
                data
            )
        }

        builder.closedOrInvalidResultHandler { ->
            handleFormResult(
                BedrockFormEvent(
                    formId = formId,
                    formType = BedrockFormType.SIMPLE,
                    player = player,
                    isClosed = true
                ),
                emptyList(),
                config,
                data
            )
        }

        return floodgate.sendForm(player.uniqueId, builder.build())
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<BedrockFormButton> {
        return config.getSubsections("buttons").map { section ->
            val imageType = section.getStringOrNull("image_type")
                ?.let { enumValueOfOrNull<FormImage.Type>(it.uppercase()) }

            val imageData = section.getStringOrNull("image")

            BedrockFormButton(
                text = section.getString("text"),
                commands = section.getStrings("commands", "command"),
                image = if (imageType != null && imageData != null) {
                    FormImage.of(imageType, imageData)
                } else {
                    null
                }
            )
        }
    }
}
