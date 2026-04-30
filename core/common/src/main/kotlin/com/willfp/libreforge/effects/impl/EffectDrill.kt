package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.VectorUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Block


object EffectDrill : MineBlockEffect<NoCompileData>("drill") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of blocks to break!")
        require("check_hardness", "You must specify if hardness should be checked!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false

        val player = data.player ?: return false

        val amount = config.getIntFromExpression("amount", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getStrings("blacklisted_blocks").map { Blocks.lookup(it) }

        val preventTriggers = config.getBool("prevent_trigger")

        val blocks = mutableSetOf<Block>()

        for (i in 1..amount) {
            val simplified = VectorUtils.simplifyVector(player.location.direction.normalize()).multiply(i)
            val toBreak = block.world.getBlockAt(block.location.clone().add(simplified))

            if (blacklist.matches(toBreak)) {
                continue
            }

            if (whitelist != null) {
                if (!whitelist.matches(toBreak)) {
                    continue
                }
            }

            if (config.getBool("check_hardness")) {
                if (toBreak.type.hardness > block.type.hardness) {
                    continue
                }
            }

            if (!AntigriefManager.canBreakBlock(player, toBreak)) {
                continue
            }

            if (toBreak.type.hardness < 0) {
                continue
            }

            blocks.add(toBreak)
        }

        player.breakBlocksSafely(blocks, preventTriggers)

        return true
    }
}
