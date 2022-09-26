@file:Suppress("HttpUrlsUsage")

package com.willfp.libreforge

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.TransientConfig
import java.io.BufferedReader
import java.io.File
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors

private val executor = Executors.newSingleThreadExecutor()

internal fun LibReforgePlugin.shareConfigs(directory: String) {
    executor.submit {
        val configs = this.getUsermadeConfigFiles(directory)

        if (configs.isNotEmpty()) {
            val key = getKey()

            for (file in configs) {
                shareConfig(file, this, key)
            }
        }
    }
}

private fun getKey(): String {
    val url = URL("http://configshare.auxilor.io/key")
    val connection = url.openConnection()

    return try {
        val stream = connection.getInputStream()
        val reader = stream.reader()
        BufferedReader(reader).readLine()
    } catch (_: Exception) {
        "" // Handle rate limit the bad way!
    }
}

private fun shareConfig(file: File, plugin: LibReforgePlugin, key: String) {
    val id = file.nameWithoutExtension
    val config = file.readText()

    val body = TransientConfig(
        mapOf(
            "id" to id,
            "plugin" to plugin.name,
            "config" to config,
            "key" to key
        ), ConfigType.JSON
    ).toPlaintext()

    val client = HttpClient.newBuilder().build()

    val request = HttpRequest.newBuilder()
        .uri(URI.create("http://configshare.auxilor.io/"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()

    try {
        client.send(request, HttpResponse.BodyHandlers.ofString())
    } catch (_: Exception) {
        // Do nothing, just let it fail.
    }
}
