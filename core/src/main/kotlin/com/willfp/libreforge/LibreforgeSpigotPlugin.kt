package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.libreforge.commands.CommandLibreforge
import com.willfp.libreforge.configs.ChainsYml
import com.willfp.libreforge.configs.TriggerPlaceholdersYml
import com.willfp.libreforge.configs.lrcdb.CommandLrcdb
import com.willfp.libreforge.display.ItemFlagDisplay
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.custom.CustomEffectArguments
import com.willfp.libreforge.effects.impl.bossbar.BossBarProgressPlaceholder
import com.willfp.libreforge.integrations.auraskills.AuraSkillsIntegration
import com.willfp.libreforge.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.integrations.axenvoy.AxEnvoyIntegration
import com.willfp.libreforge.integrations.citizens.CitizensIntegration
import com.willfp.libreforge.integrations.votifier.VotifierIntegration
import com.willfp.libreforge.integrations.custombiomes.impl.CustomBiomesTerra
import com.willfp.libreforge.integrations.custombiomes.impl.CustomBiomesTerraformGenerator
import com.willfp.libreforge.integrations.jobs.JobsIntegration
import com.willfp.libreforge.integrations.levelledmobs.LevelledMobsIntegration
import com.willfp.libreforge.integrations.mcmmo.McMMOIntegration
import com.willfp.libreforge.integrations.modelengine.ModelEngineIntegration
import com.willfp.libreforge.integrations.paper.PaperIntegration
import com.willfp.libreforge.integrations.scyther.ScytherIntegration
import com.willfp.libreforge.integrations.tab.TabIntegration
import com.willfp.libreforge.integrations.tmmobcoins.TMMobcoinsIntegration
import com.willfp.libreforge.integrations.vault.VaultIntegration
import com.willfp.libreforge.integrations.worldguard.WorldGuardIntegration
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.placeholder.*
import com.willfp.libreforge.placeholders.CustomPlaceholders
import com.willfp.libreforge.triggers.DispatchedTriggerFactory
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener

internal lateinit var plugin: LibreforgeSpigotPlugin
    private set

class LibreforgeSpigotPlugin : EcoPlugin() {
    val chainsYml = ChainsYml(this)
    val triggersPlaceholdersYml = TriggerPlaceholdersYml(this)

    val dispatchedTriggerFactory = DispatchedTriggerFactory(this)

    private var hasLoaded = false

    private val configCategories = listOf(
        LevelTypes,
        CustomEffectArguments
    )

    private val displayModule = ItemFlagDisplay(this)

    init {
        plugin = this
    }

    override fun handleLoad() {
        for (category in configCategories) {
            category.copyConfigs(this)
            category.reload(this)
        }
    }

    override fun handleEnable() {
        if (this.configYml.getBool("show-libreforge-info")) {
            this.logger.info("")
            this.logger.info("Hey, what's this plugin doing here? I didn't install it!")
            this.logger.info("libreforge is the effects system for plugins like EcoEnchants,")
            this.logger.info("EcoJobs, EcoItems, etc. If you're looking for config options for")
            this.logger.info("things like cooldown messages, lrcdb, and stuff like that, you'll")
            this.logger.info("find it under /plugins/libreforge")
            this.logger.info("")
            this.logger.info("Don't worry about updating libreforge, it's handled automatically!")
            this.logger.info("")
        }

        if (Prerequisite.HAS_PAPER.isMet) {
            PaperIntegration.load(this)
        }

        pointsPlaceholder(this).register()
        globalPointsPlaceholder(this).register()
        ItemPointsPlaceholder(this).register()
        ItemLevelPlaceholder(this).register()
        // Register required first because it technically matches the pattern of "xp"
        ItemXPRequiredPlaceholder(this).register()
        ItemXPPlaceholder(this).register()
        ItemProgressPlaceholder(this).register()
        ItemDataPlaceholder(this).register()
        BossBarProgressPlaceholder(this).register()
    }

    override fun handleReload() {
        for (config in chainsYml.getSubsections("chains")) {
            Effects.register(
                config.getString("id"),
                Effects.compileChain(
                    config.getSubsections("effects"),
                    ViolationContext(this, "chains.yml")
                ) ?: continue
            )
        }

        for (customPlaceholder in this.configYml.getSubsections("placeholders")) {
            CustomPlaceholders.load(customPlaceholder, this)
        }

        for (category in configCategories) {
            category.reload(this)
        }

        displayModule.reload()

        hasLoaded = true
    }

    override fun createTasks() {
        dispatchedTriggerFactory.startTicking()

        // Poll for changes
        val skipAFKPlayers = configYml.getBool("refresh.players.skip-afk-players")
        plugin.scheduler.runTimer(20, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (skipAFKPlayers && AFKManager.isAfk(player)) {
                    continue
                }

                player.toDispatcher().refreshHolders()
            }
        }

        if (configYml.getBool("refresh.entities.enabled")) {
            /*
            Poll for changes in entities
            Each world is offset by 3 ticks to prevent lag spikes
             */
            var currentOffset = 30L
            for (world in Bukkit.getWorlds()) {
                plugin.scheduler.runTimer(currentOffset, configYml.getInt("refresh.entities.interval").toLong()) {
                    for (entity in world.entities) {
                        if (entity is LivingEntity) {
                            entity.toDispatcher().refreshHolders()
                        }
                    }
                }
                currentOffset += 3
            }
        }

        // Poll for changes in global holders
        this.scheduler.runTimer(25, 20) {
            GlobalDispatcher.refreshHolders()
        }
    }

    override fun loadListeners(): List<Listener> {
        val listeners = mutableListOf(
            EffectDataFixer,
            ItemRefreshListener(this)
        )

        if (Prerequisite.HAS_PAPER.isMet) {
            listeners += PaperEffectDataFixer
        }

        return listeners
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("AuraSkills") { AuraSkillsIntegration.load(this) },
            IntegrationLoader("AureliumSkills") { AureliumSkillsIntegration.load(this) },
            IntegrationLoader("Jobs") { JobsIntegration.load(this) },
            IntegrationLoader("LevelledMobs") { LevelledMobsIntegration.load(this) },
            IntegrationLoader("mcMMO") { McMMOIntegration.load(this) },
            IntegrationLoader("Citizens") { CitizensIntegration.load(this) },
            IntegrationLoader("Scyther") { ScytherIntegration.load(this) },
            IntegrationLoader("TMMobcoins") { TMMobcoinsIntegration.load(this) },
            IntegrationLoader("Vault") { VaultIntegration.load(this) },
            IntegrationLoader("WorldGuard") { WorldGuardIntegration.load(this) },
            IntegrationLoader("TAB") { TabIntegration.load(this) },
            IntegrationLoader("Terra") { CustomBiomesTerra.load(this) },
            IntegrationLoader("TerraformGenerator") { CustomBiomesTerraformGenerator.load(this) },
            IntegrationLoader("AxEnvoy") { AxEnvoyIntegration.load(this) },
            IntegrationLoader("Votifier") { VotifierIntegration.load(this)},
            IntegrationLoader("ModelEngine") { ModelEngineIntegration.load(this) }
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandLrcdb(this),
            CommandLibreforge(this)
        )
    }

    override fun createDisplayModule(): DisplayModule {
        return displayModule
    }

    override fun getMinimumEcoVersion(): String {
        return "6.70.0"
    }

    /**
     * Run a runnable when the plugin is enabled.
     */
    fun runWhenEnabled(runnable: () -> Unit) {
        if (hasLoaded) {
            runnable()
        } else {
            onCreateTasks(runnable)
        }
    }
}
