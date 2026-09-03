package cx.tfe.fennec.mixin;

import cx.tfe.fennec.events.Event;
import cx.tfe.fennec.events.EventBus;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatMixin {
    @Inject(method = "addMessage", at = @At("HEAD"))
    private void onAnyChatMessage(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        EventBus.INSTANCE.post(
                new Event.OnChatMessage(contents, signature, source, tag)
        );
    }
}
