package com.willfp.libreforge.lrcdb

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.config
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig
import com.willfp.libreforge.LibreforgeConfig
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors
import java.util.concurrent.Future

private val executor = Executors.newSingleThreadExecutor()

internal fun <T> onLrcdbThread(action: () -> T): Future<T> = executor.submit(action)

private val client = HttpClient.newBuilder().build()

data class ExportableConfig(
    val name: String,
    val config: Config
) {
    fun export(plugin: EcoPlugin, private: Boolean): ExportResponse {
        val body = config(ConfigType.JSON) {
            "name" to name
            "plugin" to plugin.name
            "author" to LibreforgeConfig.getString("author")
            "contents" to config.toPlaintext()
            "isPrivate" to private
        }.toPlaintext()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://lrcdb.auxilor.io/api/v1/addConfig"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val res = try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            return ExportResponse(
                false,
                400,
                config {
                    "message" to e.message
                }
            )
        }

        val code = res.statusCode()

        val isError = code in 400..599
        val json = readConfig(res.body(), ConfigType.JSON)

        return ExportResponse(
            !isError,
            code,
            json
        )
    }
}

data class ExportResponse(
    val success: Boolean,
    val code: Int,
    val body: Config
)
