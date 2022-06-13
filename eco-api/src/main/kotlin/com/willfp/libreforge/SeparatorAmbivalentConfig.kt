package com.willfp.libreforge

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import com.willfp.eco.util.StringUtils

private class SeparatorAmbivalentConfig(
    private val config: Config
) : Config {
    private inline fun <reified T> preprocess(path: String, getter: (String) -> T): T {
        val hyphen = path.replace('_', '-')
        val underscore = path.replace('-', '_')

        return getter(hyphen) ?: getter(underscore)
    }

    override fun clone(): Config = SeparatorAmbivalentConfig(config.clone())

    override fun toPlaintext(): String = config.toPlaintext()

    override fun has(path: String): Boolean {
        val hyphen = path.replace('_', '-')
        val underscore = path.replace('-', '_')

        return config.has(hyphen) || config.has(underscore)
    }

    override fun getKeys(deep: Boolean): List<String> =
        config.getKeys(deep)

    override fun get(path: String): Any? =
        preprocess(path) { config.get(it) }

    override fun set(path: String, obj: Any?) =
        config.set(path, obj)

    override fun getSubsectionOrNull(path: String): Config? =
        preprocess(path) {
            val cfg = config.getSubsectionOrNull(it)
            if (cfg == null) null else SeparatorAmbivalentConfig(cfg)
        }

    override fun getIntOrNull(path: String): Int? =
        preprocess(path) { config.getIntOrNull(it) }

    override fun getIntsOrNull(path: String): List<Int>? =
        preprocess(path) { config.getIntsOrNull(it) }

    override fun getBoolOrNull(path: String): Boolean? =
        preprocess(path) { config.getBoolOrNull(it) }

    override fun getBoolsOrNull(path: String): List<Boolean>? =
        preprocess(path) { config.getBoolsOrNull(it) }

    override fun getStringOrNull(path: String, format: Boolean, option: StringUtils.FormatOption): String? =
        preprocess(path) { config.getStringOrNull(it, format, option) }

    override fun getStringsOrNull(
        path: String,
        format: Boolean,
        option: StringUtils.FormatOption
    ): List<String>? = preprocess(path) { config.getStringsOrNull(it, format, option) }

    override fun getDoubleOrNull(path: String): Double? =
        preprocess(path) { config.getDoubleOrNull(it) }

    override fun getDoublesOrNull(path: String): List<Double>? =
        preprocess(path) { config.getDoublesOrNull(it) }

    override fun getSubsectionsOrNull(path: String): List<Config>? =
        preprocess(path) { config.getSubsectionsOrNull(it)?.map { cfg -> SeparatorAmbivalentConfig(cfg) } }

    override fun getType(): ConfigType = config.type

    override fun addInjectablePlaceholder(placeholders: Iterable<InjectablePlaceholder>) =
        config.addInjectablePlaceholder(placeholders)

    override fun clearInjectedPlaceholders() =
        config.clearInjectedPlaceholders()

    override fun getPlaceholderInjections(): List<InjectablePlaceholder> =
        config.placeholderInjections

    override fun injectPlaceholders(vararg placeholders: StaticPlaceholder) =
        config.injectPlaceholders(*placeholders)

    override fun injectPlaceholders(vararg placeholders: InjectablePlaceholder) =
        config.injectPlaceholders(*placeholders)
}

fun Config.separatorAmbivalent(): Config = SeparatorAmbivalentConfig(this)
