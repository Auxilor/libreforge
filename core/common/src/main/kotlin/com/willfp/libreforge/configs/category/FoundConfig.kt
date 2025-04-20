package com.willfp.libreforge.configs.category

import com.willfp.eco.core.EcoPlugin
import java.io.FileOutputStream

class FoundConfig(
    name: String,
    directory: String,
    private val plugin: EcoPlugin
) {
    private val source = plugin::class.java.classLoader
    private val resourcePath = "$directory/$name.yml"

    fun copy() {
        val inputStream = source.getResourceAsStream(resourcePath) ?: return
        val outFile = plugin.dataFolder.resolve(resourcePath)

        if (!outFile.exists()) {
            outFile.parentFile.mkdirs()
            FileOutputStream(outFile).use { outStream ->
                inputStream.copyTo(outStream)
            }
        }

        inputStream.close()
    }
}
