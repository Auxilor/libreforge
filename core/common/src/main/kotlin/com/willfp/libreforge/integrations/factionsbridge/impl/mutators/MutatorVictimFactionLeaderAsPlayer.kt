package com.willfp.libreforge.integrations.factionsbridge.impl.mutators

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object MutatorVictimFactionLeaderAsPlayer : Mutator<NoCompileData>("victim_faction_leader_as_player") {
    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.PLAYER)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val api = FactionsBridge.getFactionsAPI() ?: return data
        val victim = data.victim as? Player ?: return data
        val faction = api.getFaction(victim) ?: return data
        val leader = faction.getLeader()?.getPlayer() ?: return data
        return data.copy(player = leader)
    }
}
