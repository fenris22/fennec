package cx.tfe.fennec.events

import cx.tfe.fennec.data.Biomes
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.chat.GuiMessageSource
import net.minecraft.client.multiplayer.chat.GuiMessageTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MessageSignature

abstract class Event {

    /** Unused Events
    abstract class ScreenEvent(val screen: Screen) : Event() { }
    abstract class GuiEvent(val screen: Screen) : Event() { }
    */
    class OnChatMessage(val message: Component, val signature: MessageSignature?, val source: GuiMessageSource, tag: GuiMessageTag?) : Event()
    class ClientTickEvent: Event()
    class RegionEnterEvent(val region: Biomes) : Event()
    class RegionExitEvent(val region: Biomes) : Event()
    class ServerSwitchEvent(val listener: ClientPacketListener, val client: Minecraft) : Event()

}