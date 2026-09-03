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

}
