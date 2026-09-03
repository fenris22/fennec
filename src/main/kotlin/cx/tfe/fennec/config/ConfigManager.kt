package cx.tfe.fennec.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.player.LocalPlayer.LOGGER
import java.io.File

object ConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = FabricLoader.getInstance()
        .configDir
        .resolve("fennec.json")
        .toFile()

    var config: FennecConfig = load()
        private set

    fun load(): FennecConfig {
        if (!configFile.exists()) {
            val default = FennecConfig()
            save(default)
            return default
        }

        return try {
            configFile.reader().use { reader ->
                gson.fromJson(reader, FennecConfig::class.java) ?: FennecConfig()
            }
        } catch (e: Exception) {
            val backup = File(configFile.parentFile, "fennec.json.bak")
            configFile.copyTo(backup, overwrite = true)
            LOGGER.error("Failed to parse fennec.json — backed up to fennec.json.bak and loaded defaults instead.", e)
            FennecConfig()
        }
    }

    fun save(cfg: FennecConfig = config) {
        config = cfg
        configFile.parentFile.mkdirs()
        configFile.writer().use { writer ->
            gson.toJson(cfg, writer)
        }
    }
}