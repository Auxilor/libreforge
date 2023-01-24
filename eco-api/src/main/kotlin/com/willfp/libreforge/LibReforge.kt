@file:JvmName("LibReforgeUtils")

package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.emptyConfig
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.libreforge.chains.EffectChains
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.MovementConditionListener
import com.willfp.libreforge.conditions.isMet
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.events.HolderProvideEvent
import com.willfp.libreforge.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.integrations.boosters.BoostersIntegration
import com.willfp.libreforge.integrations.ecoarmor.EcoArmorIntegration
import com.willfp.libreforge.integrations.ecobosses.EcoBossesIntegration
import com.willfp.libreforge.integrations.ecoenchants.EcoEnchantsIntegration
import com.willfp.libreforge.integrations.ecoitems.EcoItemsIntegration
import com.willfp.libreforge.integrations.ecojobs.EcoJobsIntegration
import com.willfp.libreforge.integrations.ecopets.EcoPetsIntegration
import com.willfp.libreforge.integrations.ecoskills.EcoSkillsIntegration
import com.willfp.libreforge.integrations.jobs.JobsIntegration
import com.willfp.libreforge.integrations.levelledmobs.LevelledMobsIntegration
import com.willfp.libreforge.integrations.mcmmo.McMMOIntegration
import com.willfp.libreforge.integrations.paper.PaperIntegration
import com.willfp.libreforge.integrations.reforges.ReforgesIntegration
import com.willfp.libreforge.integrations.scyther.ScytherIntegration
import com.willfp.libreforge.integrations.talismans.TalismansIntegration
import com.willfp.libreforge.integrations.tmmobcoins.TMMobcoinsIntegration
import com.willfp.libreforge.integrations.vault.VaultIntegration
import com.willfp.libreforge.lrcdb.ExportableConfig
import com.willfp.libreforge.lrcdb.LrcdbYml
import com.willfp.libreforge.lrcdb.onLrcdbThread
import com.willfp.libreforge.triggers.InvocationPlaceholderListener
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.triggers.TriggerStatic
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile

private val holderProviders = mutableListOf<HolderProvider>()

private val previousStates: MutableMap<UUID, Iterable<ConfiguredEffect>> = WeakHashMap()
private val holderCache = Caffeine.newBuilder()
    .expireAfterWrite(4L, TimeUnit.SECONDS)
    .build<UUID, Iterable<Holder>>()

typealias HolderProvider = (Player) -> Iterable<Holder>

@Suppress("UNUSED")
abstract class LibReforgePlugin @JvmOverloads constructor(
    supportsLrcdb: Boolean = true
) : EcoPlugin() {
    private val defaultPackage = StringUtils.join(
        arrayOf("com", "willfp", "libreforge"),
        "."
    )

    @Suppress("MemberVisibilityCanBePrivate")
    val chainsYml: ChainsYml by lazy { ChainsYml(this) }

    val lrcdbYml: Config by lazy {
        if (supportsLrcdb) {
            LrcdbYml(this)
        } else emptyConfig()
    }

    init {
        setInstance()

        if (this.javaClass.packageName == defaultPackage) {
            throw IllegalStateException("You must shade and relocate libreforge into your jar!")
        }

        if (Prerequisite.HAS_PAPER.isMet) {
            PaperIntegration.load()
        }
    }

    open fun handleEnableAdditional() {

    }

    open fun handleDisableAdditional() {

    }

    open fun handleReloadAdditional() {

    }

    open fun loadAdditionalIntegrations(): List<IntegrationLoader> {
        return emptyList()
    }

    fun copyConfigs(directory: String) {
        val folder = File(this.dataFolder, directory)
        if (!folder.exists()) {
            val files = getDefaultConfigNames(directory)

            for (configName in files) {
                UsermadeConfig(configName, directory, this)
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getDefaultConfigNames(directory: String): Collection<String> {
        val files = mutableListOf<String>()

        try {
            for (entry in ZipFile(this.file).entries().asIterator()) {
                if (entry.name.startsWith("$directory/")) {
                    files.add(entry.name.removePrefix("$directory/"))
                }
            }
        } catch (_: Exception) {
            // Sometimes, ZipFile likes to completely fail. No idea why, but here's the 'solution'!
        }

        files.removeIf { !it.endsWith(".yml") }
        files.replaceAll { it.replace(".yml", "") }

        return files
    }

    private fun doFetchConfigs(directory: String): Map<String, Config> {
        val configs = mutableMapOf<String, Config>()

        for (file in File(this.dataFolder, directory).walk()) {
            if (file.nameWithoutExtension == "_example") {
                continue
            }

            if (!file.name.endsWith(".yml")) {
                continue
            }

            val id = file.nameWithoutExtension
            val config = file.readConfig(ConfigType.YAML)
            configs[id] = config
        }

        return configs
    }

    fun fetchConfigs(directory: String, dontShare: Boolean = false): Map<String, Config> {
        // Share configs on fetch
        val fetched = doFetchConfigs(directory)

        val isSharing = this.lrcdbYml.getBool("share-configs.enabled")
        val isPublic = this.lrcdbYml.getBool("share-configs.publicly")

        if (isSharing && !dontShare) {
            onLrcdbThread {
                for ((name, config) in fetched) {
                    ExportableConfig(name, config)
                        .export(this, !isPublic)
                }
            }
        }

        return fetched
    }

    fun getUsermadeConfigs(directory: String): Map<String, Config> {
        val all = doFetchConfigs(directory).toMutableMap()

        for (name in getDefaultConfigNames(directory)) {
            all.remove(name)
        }

        return all
    }

    fun getUsermadeConfigFiles(directory: String): Collection<File> {
        val names = doFetchConfigs(directory).keys - getDefaultConfigNames(directory).toSet()

        return File(this.dataFolder, directory).walk().filter {
            it.nameWithoutExtension in names
        }.toList()
    }

    fun registerHolderProvider(provider: HolderProvider) {
        holderProviders.add(provider)
    }

    fun registerJavaHolderProvider(provider: java.util.function.Function<Player, Iterable<Holder>>) {
        holderProviders.add(provider::apply)
    }

    fun logViolation(id: String, context: ViolationContext, violation: ConfigViolation) {
        this.logger.warning("")
        this.logger.warning("Invalid configuration for $id in context $context:")
        this.logger.warning("(Cause) Argument '${violation.param}'")
        this.logger.warning("(Reason) ${violation.message}")
        this.logger.warning("")
    }

    final override fun handleEnable() {
        initPointPlaceholders()
        this.eventManager.registerListener(TridentHolderDataAttacher(this))
        this.eventManager.registerListener(MovementConditionListener())
        this.eventManager.registerListener(HitsTracker(this))
        this.eventManager.registerListener(EffectCollisionFixer(this))
        this.eventManager.registerListener(InvocationPlaceholderListener)
        for (condition in Conditions.values()) {
            this.eventManager.registerListener(condition)
        }
        for (effect in Effects.values()) {
            this.eventManager.registerListener(effect)
        }
        for (trigger in Triggers.values()) {
            this.eventManager.registerListener(trigger)
        }

        handleEnableAdditional()
    }

    final override fun handleDisable() {
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                for (effect in player.getActiveEffects()) {
                    effect.disableFor(player)
                }
            } catch (e: Exception) {
                Bukkit.getLogger().warning("Error disabling effects, not important - do not report this")
            }
        }

        handleDisableAdditional()
    }

    final override fun handleReload() {
        this.scheduler.runTimer(30, 30) {
            for (player in Bukkit.getOnlinePlayers()) {
                player.updateEffects()
            }
        }
        TriggerStatic.beginTiming(this)
        Effects.TRACEBACK.init()

        for (config in chainsYml.getSubsections("chains")) {
            EffectChains.compile(config, "chains.yml")
        }

        handleReloadAdditional()
    }

    final override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        val integrations = mutableListOf(
            IntegrationLoader("EcoSkills", EcoSkillsIntegration::load),
            IntegrationLoader("AureliumSkills", AureliumSkillsIntegration::load),
            IntegrationLoader("mcMMO", McMMOIntegration::load),
            IntegrationLoader("Jobs", JobsIntegration::load),
            IntegrationLoader("Vault", VaultIntegration::load),
            IntegrationLoader("TMMobcoins", TMMobcoinsIntegration::load),
            IntegrationLoader("EcoArmor", EcoArmorIntegration::load),
            IntegrationLoader("EcoBosses", EcoBossesIntegration::load),
            IntegrationLoader("Talismans", TalismansIntegration::load),
            IntegrationLoader("EcoItems", EcoItemsIntegration::load),
            IntegrationLoader("Reforges", ReforgesIntegration::load),
            IntegrationLoader("Boosters", BoostersIntegration::load),
            IntegrationLoader("EcoEnchants", EcoEnchantsIntegration::load),
            IntegrationLoader("EcoPets", EcoPetsIntegration::load),
            IntegrationLoader("EcoJobs", EcoJobsIntegration::load),
            IntegrationLoader("LevelledMobs") { LevelledMobsIntegration.load(this) },
            IntegrationLoader("Scyther", ScytherIntegration::load),
        )

        integrations.addAll(loadAdditionalIntegrations())

        return integrations
    }

    private fun setInstance() {
        instance = this
    }

    override fun mutateProps(props: PluginProps): PluginProps {
        return props.apply {
            isSupportingExtensions = true
        }
    }

    override fun getMinimumEcoVersion(): String {
        return "6.48.0"
    }

    companion object {
        @JvmStatic
        internal lateinit var instance: LibReforgePlugin
    }
}

private fun Player.getPureHolders(): Iterable<Holder> {
    return holderCache.get(this.uniqueId) {
        val holders = holderProviders.flatMap { it(this) }

        val event = HolderProvideEvent(this, holders.toMutableList())
        Bukkit.getPluginManager().callEvent(event)
        event.holders
    }
}

fun Player.purgeHolders() {
    holderCache.invalidate(this.uniqueId)
    previousStates.remove(this.uniqueId)
}

@JvmOverloads
fun Player.getHolders(respectConditions: Boolean = true): Iterable<Holder> =
    this.getPureHolders()
        .filter { if (respectConditions) it.conditions.all { cond -> cond.isMet(this) } else true }

fun Player.getActiveEffects(): Iterable<ConfiguredEffect> =
    this.getHolders(respectConditions = true)
        .flatMap { it.effects }
        .filter { it.conditions.all { cond -> cond.isMet(this) } }

@JvmOverloads
fun Player.updateEffects(noRescan: Boolean = false) {
    val before = mutableListOf<ConfiguredEffect>()
    before.addAll(previousStates[this.uniqueId] ?: emptyList())

    if (!noRescan) {
        holderCache.invalidate(this.uniqueId)
    }

    val after = this.getActiveEffects()
    previousStates[this.uniqueId] = after

    val added = after.toMutableList()
    for (effect in before) {
        added.remove(effect)
    }

    val removed = before.toMutableList()
    for (effect in after) {
        removed.remove(effect)
    }

    for (effect in added) {
        if (!effect.conditions.isMet(this)) {
            continue
        }

        effect.enableFor(this)
    }

    for (effect in removed) {
        effect.disableFor(this)
    }

    for (effect in after) {
        if (!effect.conditions.isMet(this)) {
            effect.disableFor(this)
        }
        effect.refreshFor(this)
    }
}
