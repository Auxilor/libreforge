package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpMetaData
import org.bukkit.entity.Player

object ConditionLpMetaBelow : Condition<NoCompileData>("lp_meta_below") {
    override val description = "Passes when the numeric LuckPerms meta value is at or below the maximum."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Fails if the meta key is not set or is not a number."
    )

    override val arguments = arguments {
        require(
            "key",
            "You must specify the meta key!",
            description = "The LuckPerms meta key to read.",
            type = ArgType.STRING,
            example = "mine-tier"
        )
        require(
            "value",
            "You must specify the maximum value!",
            description = "The maximum value the meta must have.",
            type = ArgType.EXPRESSION,
            example = "5"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val metaData = player.lpMetaData ?: return false
        val meta = metaData.getMetaValue(config.getString("key"))?.toDoubleOrNull() ?: return false

        return meta <= config.getDoubleFromExpression("value", player)
    }
}
