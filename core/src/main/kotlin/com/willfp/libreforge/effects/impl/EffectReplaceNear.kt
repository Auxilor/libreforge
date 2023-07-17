package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.BlockFace

object EffectReplaceNear : MineBlockEffect<NoCompileData>("replace_near") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius to replace!")
        require("replace_to", "You must specify the block to replace to!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radius = config.getIntFromExpression("radius", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val replaceTo = Material.matchMaterial(config.getString("replace_to").uppercase()) ?: Material.APPLE

        val whitelist = config.getStringsOrNull("whitelist")

        val duration = config.getDoubleFromExpression("duration")

        val exposed = config.getBool("exposed_only")

        for (x in (-radius..radius)) {
            for (y in (-radius..radius)) {
                for (z in (-radius..radius)) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    val toReplace = block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )

                    if (config.getStrings("blacklisted_blocks").containsIgnoreCase(toReplace.type.name)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.containsIgnoreCase(toReplace.type.name)) {
                            continue
                        }
                    }

                    if (toReplace.type == Material.AIR) {
                        continue
                    }

                    if (!toReplace.getRelative(BlockFace.UP).isEmpty && exposed) {
                        continue
                    }

                    if (!AntigriefManager.canBreakBlock(player, toReplace)) {
                        continue
                    }

                    if (duration > 0) {
                        val oldBlock = toReplace.type
                        plugin.scheduler.runLater(duration.toLong()) {
                            toReplace.type = oldBlock
                        }
                    }

                    toReplace.type = replaceTo
                }
            }
        }

        return true
    }
}
