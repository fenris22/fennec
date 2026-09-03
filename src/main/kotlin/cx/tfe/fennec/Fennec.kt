package cx.tfe.fennec

import cx.tfe.fennec.commands.mainCommand
import cx.tfe.fennec.events.Event
import cx.tfe.fennec.events.EventBus
import cx.tfe.fennec.features.impl.safari.SafariOverlay
import cx.tfe.fennec.util.trackers.BiomeTracker
import cx.tfe.fennec.util.trackers.CritterTracker
import cx.tfe.fennec.util.trackers.CritterDetectionTracker
import cx.tfe.fennec.util.trackers.SafariInstanceTracker
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory

object Fennec : ModInitializer {

	const val DEV_MODE: Boolean = false
	const val MOD_ID: String = "fennec"

	private val LOGGER = LoggerFactory.getLogger(MOD_ID)

	@JvmStatic
	val mc: Minecraft = Minecraft.getInstance()

	override fun onInitialize() {

		//Fabric API EventBus Posting
		ClientTickEvents.END_CLIENT_TICK.register { EventBus.post(Event.ClientTickEvent()) }
		ClientPlayConnectionEvents.JOIN.register { listener, _, client -> EventBus.post(Event.ServerSwitchEvent(listener, client)) }

		//Commands
		ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher, _ ->
			arrayOf(
				mainCommand
			).forEach { commodore -> commodore.register(dispatcher) }
		})
		// Drawing is registered once, not on module toggle — SafariOverlay.render()
		// checks SafariInstanceTracker.inSafariInstance itself rather than `enabled`.
		HudElementRegistry.addLast(id("safari_overlay"), SafariOverlay::render)

		//Register trackers
		EventBus.register(CritterTracker)
		EventBus.register(BiomeTracker)
		EventBus.register(SafariInstanceTracker)
		EventBus.register(CritterDetectionTracker)

		LOGGER.info("Initialized fennec mod!")
	}

	fun id(path: String): Identifier
			= Identifier.fromNamespaceAndPath(MOD_ID, path)
}