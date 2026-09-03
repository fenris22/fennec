package cx.tfe.fennec.util

import cx.tfe.fennec.Fennec.mc
import cx.tfe.fennec.features.impl.Debug
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object Helpers {
    //Tell the user something in client chat
    fun tell(message: String) { mc.gui.chat.addClientSystemMessage(Component.literal("§3[§fFennec§3]§7 $message")) }

    //debug
    fun debug(message: String) { if (Debug.enabled) { mc.gui.chat.addClientSystemMessage(Component.literal("§3[§4Debug§3]§7 $message")) } }

    //Removes minecraft color code symbols
    fun String.noControlCodes(): String = replace(Regex("§."), "")

    /**
     * Sends a command as if the player typed it in chat. `command` should
     * NOT include the leading "/" — sendCommand expects the bare command
     * text, same as vanilla's ChatScreen strips the "/" before calling this.
     *
     * Uses the signed path (sendCommand), which is what a real player
     * typing a command in chat goes through. If that ever causes problems
     * — e.g. no signing key available, or you just don't care about
     * signing for a custom plugin command like this one — swap to
     * connection.sendUnsignedCommand(command) instead, which returns a
     * Boolean for whether it actually sent.
     */
    fun sendCommand(command: String) { mc.player?.connection?.sendCommand(command.removePrefix("/")) }



}
